package com.expensesharing.com.expensesharing.controller;


import com.expensesharing.com.expensesharing.entity.Expense;
import com.expensesharing.com.expensesharing.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@Tag(name = "Expenses", description = "Record expenses, split them, and generate balance sheets")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Record a new expense and split it (EQUAL / EXACT / PERCENTAGE)")
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense) {
        return new ResponseEntity<>(expenseService.addExpense(expense), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an expense by ID")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return new ResponseEntity<>(expenseService.getExpenseById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all expenses")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return new ResponseEntity<>(expenseService.getAllExpenses(), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get expenses a given user participates in")
    public ResponseEntity<List<Expense>> getExpensesByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(expenseService.getExpensesByUserId(userId), HttpStatus.OK);
    }



    // Description of this method is in service layer..
    @GetMapping("/balance-sheet")
    @Operation(summary = "Generate a consolidated balance sheet across all expenses")
    public ResponseEntity<List<Map<String, Object>>> downloadBalanceSheet() {
        List<Map<String, Object>> balanceSheetData = expenseService.generateBalanceSheetData();
        return new ResponseEntity<>(balanceSheetData, HttpStatus.OK);
    }


    // This is the way where i can store the data in the csv file everytime....
//    @GetMapping("/balance-sheet")
//    public ResponseEntity<String> generateBalanceSheet() {
//        String filePath = expenseService.generateAndSaveBalanceSheet();
//        return new ResponseEntity<>("Balance sheet generated at: " + filePath, HttpStatus.OK);
//    }


//    @GetMapping("/balance-sheet")
//    public ResponseEntity<byte[]> downloadBalanceSheet() {
//        String balanceSheetContent = expenseService.generateBalanceSheet();
//
//        byte[] output = balanceSheetContent.getBytes(StandardCharsets.UTF_8);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=balance-sheet.csv");
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
//
//        return new ResponseEntity<>(output, headers, HttpStatus.OK);
//    }
}