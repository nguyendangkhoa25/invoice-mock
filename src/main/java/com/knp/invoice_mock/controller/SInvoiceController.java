package com.knp.invoice_mock.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/InvoiceWS/")
@RequiredArgsConstructor
@Slf4j
public class SInvoiceController {
    /**
     * Mock endpoint for creating invoices in SInvoice system.
     * Returns a successful response with invoice details.
     *
     * @param request the invoice request payload (not used in mock implementation)
     * @return ResponseEntity with mocked SInvoice response
     */
    @PostMapping("createInvoice/fixed")
    public ResponseEntity<Map<String, Object>> createInvoice(@RequestBody Map<String, Object> request) {
        log.info("Mock SInvoice createInvoice endpoint called supplierTaxCode");

        Map<String, Object> response = buildSInvoiceResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * Builds the mock SInvoice API response.
     *
     * @return Map containing the mock response structure
     */
    private Map<String, Object> buildSInvoiceResponse() {
        Map<String, Object> result = new HashMap<>();
        result.put("supplierTaxCode", "3703135239");
        result.put("invoiceNo", "C25MNP56");
        result.put("transactionID", "176559280633249835");
        result.put("reservationCode", "94L3DJLCHHYFVJC");
        result.put("codeOfTax", "M2-25-BNFQQ-00000000059");

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", null);
        response.put("code", null);
        response.put("description", null);
        response.put("message", null);
        response.put("result", result);

        return response;
    }

    /**
     * Alternative endpoint that generates random invoice numbers for each request.
     * Useful for testing multiple invoice creations.
     *
     * @param request the invoice request payload (not used in mock implementation)
     * @return ResponseEntity with mocked SInvoice response with random values
     */
    @PostMapping("createInvoice/{supplierTaxCode}")
    public ResponseEntity<Map<String, Object>> createInvoiceRandom(@RequestBody Map<String, Object> request, @PathVariable String supplierTaxCode) {
        log.info("Mock SInvoice createInvoice (random) endpoint called");

        Map<String, Object> result = new HashMap<>();
        result.put("supplierTaxCode", "3703135239");
        result.put("invoiceNo", generateRandomInvoiceNo());
        result.put("transactionID", generateRandomTransactionID());
        result.put("reservationCode", generateRandomReservationCode());
        result.put("codeOfTax", generateRandomCodeOfTax());

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", null);
        response.put("code", null);
        response.put("description", null);
        response.put("message", null);
        response.put("result", result);

        return ResponseEntity.ok(response);
    }

    /**
     * Mock endpoint that returns an error response.
     * Useful for testing error handling scenarios.
     *
     * @param request the invoice request payload
     * @return ResponseEntity with mocked error response
     */
    @PostMapping("createInvoice/error")
    public ResponseEntity<Map<String, Object>> createInvoiceError(@RequestBody Map<String, Object> request) {
        log.warn("Mock SInvoice createInvoice (error) endpoint called");

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", "ERR001");
        response.put("code", "INVALID_INVOICE");
        response.put("description", "Invalid invoice data");
        response.put("message", "Failed to create invoice: Invalid tax code");
        response.put("result", null);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Health check endpoint for the mock SInvoice service.
     *
     * @return ResponseEntity indicating the service is up
     */
    @GetMapping("health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Mock SInvoice API");
        return ResponseEntity.ok(response);
    }

    // Helper methods for generating random values

    private String generateRandomInvoiceNo() {
        return "C25MNP" + System.currentTimeMillis() % 1000;
    }

    private String generateRandomTransactionID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateRandomReservationCode() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 14)
                .toUpperCase();
    }

    private String generateRandomCodeOfTax() {
        return "M2-25-" + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase()
                + "-" + String.format("%08d", System.currentTimeMillis() % 100000000);
    }
}
