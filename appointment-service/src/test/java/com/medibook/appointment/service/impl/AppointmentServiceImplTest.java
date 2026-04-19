package com.medibook.appointment.service.impl;

import com.medibook.appointment.client.SlotClient;
import com.medibook.appointment.dto.AppointmentRequest;
import com.medibook.appointment.dto.SlotDto;
import com.medibook.appointment.entity.Appointment;
import com.medibook.appointment.messaging.AppointmentEventPublisher;
import com.medibook.appointment.repository.AppointmentRepository;
import com.medibook.exception.BadRequestException;
import com.medibook.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentServiceImpl.
 *
 * Mocks: AppointmentRepository, SlotClient, AppointmentEventPublisher
 * Verifies: booking validation, cancel/complete state-machine,
 *           RabbitMQ event publishing, and exception paths.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository    appointmentRepository;
    @Mock private SlotClient               slotClient;
    @Mock private AppointmentEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private SlotDto availableSlot;
    private Appointment scheduledAppointment;

    @BeforeEach
    void setUp() {
        availableSlot = new SlotDto();
        availableSlot.setSlotId(10);
        availableSlot.setProviderId(5);
        availableSlot.setDate(LocalDate.now().plusDays(1));
        availableSlot.setStartTime(LocalTime.of(10, 0));
        availableSlot.setEndTime(LocalTime.of(10, 30));
        availableSlot.setBooked(false);
        availableSlot.setBlocked(false);

        scheduledAppointment = Appointment.builder()
                .appointmentId(1)
                .patientId(2)
                .providerId(5)
                .slotId(10)
                .status("SCHEDULED")
                .appointmentDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .modeOfConsultation("In-Person")
                .serviceType("General Consultation")
                .build();
    }

    /* ── bookAppointment() ──────────────────────────────────── */

    @Test
    @DisplayName("bookAppointment: success — saves appointment and publishes BOOKED event")
    void bookAppointment_success() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientId(2);
        req.setProviderId(5);
        req.setSlotId(10);
        req.setServiceType("General Consultation");
        req.setModeOfConsultation("In-Person");

        when(slotClient.getSlotById(10)).thenReturn(availableSlot);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(scheduledAppointment);

        Appointment result = appointmentService.bookAppointment(req);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("SCHEDULED");
        verify(slotClient).bookSlot(10);
        verify(eventPublisher).publishBooked(any(Appointment.class));
    }

    @Test
    @DisplayName("bookAppointment: throws BadRequestException when slot is already booked")
    void bookAppointment_slotAlreadyBooked_throwsException() {
        availableSlot.setBooked(true);
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientId(2); req.setProviderId(5); req.setSlotId(10);

        when(slotClient.getSlotById(10)).thenReturn(availableSlot);

        assertThatThrownBy(() -> appointmentService.bookAppointment(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already booked");

        verify(appointmentRepository, never()).save(any());
        verify(eventPublisher, never()).publishBooked(any());
    }

    @Test
    @DisplayName("bookAppointment: throws BadRequestException when slot is blocked")
    void bookAppointment_slotBlocked_throwsException() {
        availableSlot.setBlocked(true);
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientId(2); req.setProviderId(5); req.setSlotId(10);

        when(slotClient.getSlotById(10)).thenReturn(availableSlot);

        assertThatThrownBy(() -> appointmentService.bookAppointment(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("blocked");
    }

    @Test
    @DisplayName("bookAppointment: throws BadRequestException when slot belongs to different provider")
    void bookAppointment_wrongProvider_throwsException() {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientId(2); req.setProviderId(99); req.setSlotId(10);

        when(slotClient.getSlotById(10)).thenReturn(availableSlot);

        assertThatThrownBy(() -> appointmentService.bookAppointment(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("does not belong");
    }

    /* ── cancelAppointment() ────────────────────────────────── */

    @Test
    @DisplayName("cancelAppointment: success — status set to CANCELLED and event published")
    void cancelAppointment_success() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(scheduledAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(scheduledAppointment);

        appointmentService.cancelAppointment(1);

        verify(slotClient).releaseSlot(10);
        verify(eventPublisher).publishCancelled(any(Appointment.class));
    }

    @Test
    @DisplayName("cancelAppointment: throws BadRequestException when already cancelled")
    void cancelAppointment_alreadyCancelled_throwsException() {
        scheduledAppointment.setStatus("CANCELLED");
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(scheduledAppointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    @DisplayName("cancelAppointment: throws BadRequestException when appointment is completed")
    void cancelAppointment_completed_throwsException() {
        scheduledAppointment.setStatus("COMPLETED");
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(scheduledAppointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot cancel a completed");
    }

    /* ── completeAppointment() ──────────────────────────────── */

    @Test
    @DisplayName("completeAppointment: success — status set to COMPLETED and event published")
    void completeAppointment_success() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(scheduledAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(scheduledAppointment);

        appointmentService.completeAppointment(1);

        verify(appointmentRepository).save(argThat(a -> a.getStatus().equals("COMPLETED")));
        verify(eventPublisher).publishCompleted(any(Appointment.class));
    }

    @Test
    @DisplayName("completeAppointment: throws BadRequestException when not SCHEDULED")
    void completeAppointment_notScheduled_throwsException() {
        scheduledAppointment.setStatus("CANCELLED");
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(scheduledAppointment));

        assertThatThrownBy(() -> appointmentService.completeAppointment(1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only SCHEDULED");
    }

    /* ── getById() ──────────────────────────────────────────── */

    @Test
    @DisplayName("getById: returns appointment when found")
    void getById_found() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(scheduledAppointment));
        Appointment result = appointmentService.getById(1);
        assertThat(result.getAppointmentId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getById: throws ResourceNotFoundException when not found")
    void getById_notFound_throwsException() {
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> appointmentService.getById(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /* ── getByPatient() ─────────────────────────────────────── */

    @Test
    @DisplayName("getByPatient: returns all appointments for given patientId")
    void getByPatient_returnsList() {
        when(appointmentRepository.findByPatientId(2)).thenReturn(List.of(scheduledAppointment));
        List<Appointment> result = appointmentService.getByPatient(2);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(2);
    }

    /* ── getAppointmentCount() ──────────────────────────────── */

    @Test
    @DisplayName("getAppointmentCount: returns correct count for provider")
    void getAppointmentCount_returnsCount() {
        when(appointmentRepository.countByProviderId(5)).thenReturn(7L);
        int count = appointmentService.getAppointmentCount(5);
        assertThat(count).isEqualTo(7);
    }
}
