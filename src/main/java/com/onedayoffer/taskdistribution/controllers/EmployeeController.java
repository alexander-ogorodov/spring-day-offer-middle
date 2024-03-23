package com.onedayoffer.taskdistribution.controllers;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.services.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "employees")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDTO> getEmployees(@RequestParam(required = false) String sort) {
        return employeeService.getEmployees(sort);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDTO getOneEmployee(@PathVariable Integer id) {
        return employeeService.getOneEmployee(id);
    }

    @GetMapping("{id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getTasksByEmployeeId(@PathVariable Integer id) {
        return employeeService.getTasksByEmployeeId(id);
    }

    @PatchMapping("{id}/tasks/{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    public void changeTaskStatus(@PathVariable Integer id, @PathVariable Integer taskId, @RequestParam String newStatus) {
        TaskStatus status = TaskStatus.valueOf(newStatus);
        employeeService.changeTaskStatus(id, taskId, status);
    }

    @PostMapping("{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void postNewTask(@PathVariable Integer id, @RequestBody TaskDTO taskDTO) {
        employeeService.postNewTask(id, taskDTO);
    }
}