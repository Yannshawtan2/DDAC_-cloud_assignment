package com.example.qualification.service;

import com.example.qualification.dto.CreateQualificationRequestDto;
import com.example.qualification.dto.QualificationRequestDto;
import com.example.qualification.model.QualificationRequest;
import com.example.qualification.model.User;
import com.example.qualification.repository.QualificationRequestRepository;
import com.example.qualification.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QualificationRequestServiceTest {

    @Mock
    private QualificationRequestRepository qualificationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QualificationRequestService qualificationRequestService;

    private CreateQualificationRequestDto createDto;
    private QualificationRequest qualificationRequest;

    @BeforeEach
    void setUp() {
        createDto = new CreateQualificationRequestDto();
        createDto.setApplicantName("Dr. John Doe");
        createDto.setApplicantEmail("john.doe@example.com");
        createDto.setRequestedRole(User.Role.DOCTOR);
        createDto.setLicenseNumber("LIC123456");
        createDto.setLicenseType("Medical License");
        createDto.setPassword("password123");
        createDto.setS3FileKey("test-file-key");
        createDto.setOriginalFilename("license.pdf");
        createDto.setFileContentType("application/pdf");

        qualificationRequest = new QualificationRequest();
        qualificationRequest.setId(1L);
        qualificationRequest.setApplicantName("Dr. John Doe");
        qualificationRequest.setApplicantEmail("john.doe@example.com");
        qualificationRequest.setRequestedRole(User.Role.DOCTOR);
        qualificationRequest.setStatus(QualificationRequest.Status.PENDING);
    }

    @Test
    void testCreateRequest_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(qualificationRequestRepository.findByApplicantEmailAndStatus(anyString(), any()))
                .thenReturn(Optional.empty());
        when(qualificationRequestRepository.save(any(QualificationRequest.class)))
                .thenReturn(qualificationRequest);

        QualificationRequestDto result = qualificationRequestService.createRequest(createDto);

        assertNotNull(result);
        assertEquals("Dr. John Doe", result.getApplicantName());
        assertEquals("john.doe@example.com", result.getApplicantEmail());
        verify(qualificationRequestRepository).save(any(QualificationRequest.class));
    }

    @Test
    void testCreateRequest_UserAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            qualificationRequestService.createRequest(createDto);
        });
    }

    @Test
    void testGetAllRequests() {
        List<QualificationRequest> requests = Arrays.asList(qualificationRequest);
        when(qualificationRequestRepository.findAllByOrderBySubmittedAtDesc()).thenReturn(requests);

        List<QualificationRequestDto> result = qualificationRequestService.getAllRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. John Doe", result.get(0).getApplicantName());
    }

    @Test
    void testGetRequestById_Found() {
        when(qualificationRequestRepository.findById(1L)).thenReturn(Optional.of(qualificationRequest));

        Optional<QualificationRequestDto> result = qualificationRequestService.getRequestById(1L);

        assertTrue(result.isPresent());
        assertEquals("Dr. John Doe", result.get().getApplicantName());
    }

    @Test
    void testGetRequestById_NotFound() {
        when(qualificationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<QualificationRequestDto> result = qualificationRequestService.getRequestById(1L);

        assertFalse(result.isPresent());
    }
}
