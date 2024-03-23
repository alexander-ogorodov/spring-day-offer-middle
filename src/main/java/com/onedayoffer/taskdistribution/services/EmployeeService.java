package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.repositories.EmployeeRepository;
import com.onedayoffer.taskdistribution.repositories.TaskRepository;
import com.onedayoffer.taskdistribution.repositories.entities.Employee;
import com.onedayoffer.taskdistribution.repositories.entities.Task;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final Logger logger = LogManager.getLogger(EmployeeService.class);

    public List<EmployeeDTO> getEmployees(@Nullable String sortDirection) {
        List<Employee> employees;

        if (!StringUtils.isEmpty(sortDirection)) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            employees = employeeRepository.findAllAndSort(Sort.by(direction, "fio"));
        } else {
            employees = employeeRepository.findAll();
        }
        Type listType = new TypeToken<List<EmployeeDTO>>() {}.getType();
        return modelMapper.map(employees, listType);
    }

    @Transactional
    public EmployeeDTO getOneEmployee(Integer id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (optionalEmployee.isPresent()) {
            Type listType = new TypeToken<EmployeeDTO>() {}.getType();
            return modelMapper.map(optionalEmployee.get(), listType);
        } else {
            logger.error(String.format("Не найден сотрудник с id = %s", id));
            throw new NoSuchElementException(String.valueOf(id));
        }
    }

    public List<TaskDTO> getTasksByEmployeeId(Integer id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            Type listType = new TypeToken<List<TaskDTO>>() {}.getType();
            return modelMapper.map(employee.getTasks(), listType);
        } else {
            logger.error(String.format("Не найден сотрудник с id = %s", id));
            throw new NoSuchElementException(String.valueOf(id));
        }
    }

    @Transactional
    public void changeTaskStatus(Integer employeeId, Integer taskId, TaskStatus status) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            List<Task> tasks = employee.getTasks();

            Optional<Task> optionalTask = tasks.stream().filter(e -> Objects.equals(e.getId(), taskId)).findFirst();
            if (optionalTask.isPresent()) {
                Task task = optionalTask.get();
                task.setStatus(status);
                taskRepository.save(task);
            } else {
                logger.error(String.format("Не найдена задача с id = %s у сотрудника с id = %s", taskId, employeeId));
                throw new NoSuchElementException(String.valueOf(taskId));
            }
        } else {
            logger.error(String.format("Не найден сотрудник с id = %s", employeeId));
            throw new NoSuchElementException(String.valueOf(employeeId));
        }
    }

    @Transactional
    public void postNewTask(Integer employeeId, TaskDTO newTask) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            Type listType = new TypeToken<Task>() {}.getType();
            Task task = modelMapper.map(newTask, listType);

            employee.addTask(task);

        } else {
            logger.error(String.format("Не найден сотрудник с id = %s", employeeId));
            throw new NoSuchElementException(String.valueOf(employeeId));
        }
    }
}
