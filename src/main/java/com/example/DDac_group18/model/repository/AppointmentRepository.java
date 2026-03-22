package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
        List<Appointment> findByUserId(String userId);

        List<Appointment> findByDoctorId(String doctorId);

        @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.date = :date AND a.status = 'CONFIRMED'")
        List<Appointment> findConfirmedAppointmentsByDoctorAndDate(
                        @Param("doctorId") String doctorId,
                        @Param("date") LocalDate date);

        @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.date = :date")
        List<Appointment> findAllAppointmentsByDoctorAndDate(
                        @Param("doctorId") String doctorId,
                        @Param("date") LocalDate date);
}