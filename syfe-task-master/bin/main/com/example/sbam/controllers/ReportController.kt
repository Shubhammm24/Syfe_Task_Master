package com.example.sbam.controllers

import com.example.sbam.dtos.MonthlyReportResponse
import com.example.sbam.dtos.YearlyReportResponse
import com.example.sbam.services.ReportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val reportService: ReportService
) {

    @GetMapping("/monthly/{year}/{month}")
    fun getMonthlyReport(
        @PathVariable year: Int,
        @PathVariable month: Int
    ): ResponseEntity<MonthlyReportResponse> {
        val report = reportService.getMonthlyReport(year, month)
        return ResponseEntity.ok(report)
    }

    @GetMapping("/yearly/{year}")
    fun getYearlyReport(
        @PathVariable year: Int
    ): ResponseEntity<YearlyReportResponse> {
        val report = reportService.getYearlyReport(year)
        return ResponseEntity.ok(report)
    }
}
