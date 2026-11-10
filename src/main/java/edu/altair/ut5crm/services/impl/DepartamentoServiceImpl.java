package edu.altair.ut5crm.services.impl;

import edu.altair.ut5crm.dtos.DepartamentoRequestDto;
import edu.altair.ut5crm.dtos.DepartamentoResponseDto;
import edu.altair.ut5crm.entities.Departamento;
import edu.altair.ut5crm.exceptions.DuplicateResourceException;
import edu.altair.ut5crm.exceptions.ResourceNotFoundException;
import edu.altair.ut5crm.repositories.DepartamentoRepository;
import edu.altair.ut5crm.services.DepartamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoServiceImpl(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoResponseDto> obtenerTodos() {
        return departamentoRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartamentoResponseDto obtenerPorId(Long id) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el departamento con ID: " + id));
        return mapToResponseDto(departamento);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartamentoResponseDto obtenerPorCodigo(String codigo) {
        Departamento departamento = departamentoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el departamento con código: " + codigo));
        return mapToResponseDto(departamento);
    }

    @Override
    @Transactional
    public DepartamentoResponseDto crear(DepartamentoRequestDto dto) {
        if (dto.codigo() != null && departamentoRepository.existsByCodigo(dto.codigo())) {
            throw new DuplicateResourceException("Ya existe un departamento con el código: " + dto.codigo());
        }

        Departamento departamento = new Departamento();
        departamento.setCodigo(dto.codigo());

        Departamento guardado = departamentoRepository.save(departamento);
        return mapToResponseDto(guardado);
    }

    @Override
    @Transactional
    public DepartamentoResponseDto actualizar(Long id, DepartamentoRequestDto dto) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el departamento con ID: " + id));

        if (dto.codigo() != null && !departamento.getCodigo().equalsIgnoreCase(dto.codigo()) && departamentoRepository.existsByCodigo(dto.codigo())) {
            throw new DuplicateResourceException("El código " + dto.codigo() + " ya está en uso por otro departamento.");
        }

        departamento.setCodigo(dto.codigo());

        Departamento actualizado = departamentoRepository.save(departamento);
        return mapToResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!departamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Imposible eliminar. Departamento no encontrado con ID: " + id);
        }
        departamentoRepository.deleteById(id);
    }

    private DepartamentoResponseDto mapToResponseDto(Departamento departamento) {
        Set<String> nombresProfesores = departamento.getProfesores() != null
                ? departamento.getProfesores().stream()
                .map(p -> p.getNombre() + " " + p.getApellidos())
                .collect(Collectors.toSet())
                : Set.of();

        return new DepartamentoResponseDto(
                departamento.getIdDepartamento(),
                departamento.getCodigo(),
                nombresProfesores
        );
    }
}