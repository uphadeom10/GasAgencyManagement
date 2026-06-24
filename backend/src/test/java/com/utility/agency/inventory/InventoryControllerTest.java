package com.utility.agency.inventory;

import com.agency.management.inventory.controller.InventoryController;
import com.agency.management.inventory.dto.request.NewConnectionDto;
import com.agency.management.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    void testCreateNewConnection_ReturnsSuccessResponse() {
        NewConnectionDto dto = new NewConnectionDto();
        dto.setIsNewConnection(true);
        dto.setNewConnectionDetailsDtoList(Collections.emptyList());

        ResponseEntity<?> mockResponse = ResponseEntity.ok("Mocked Response");
        when(inventoryService.createNewConnection(dto)).thenReturn((ResponseEntity)mockResponse);

        ResponseEntity<?> response = inventoryController.createNewConnection(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Mocked Response", response.getBody());
        verify(inventoryService, times(1)).createNewConnection(dto);
    }
}
