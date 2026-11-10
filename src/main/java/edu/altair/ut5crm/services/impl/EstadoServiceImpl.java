package edu.altair.ut5crm.services.impl;

import edu.altair.ut5crm.dtos.EstadoRequestDto;
import edu.altair.ut5crm.dtos.EstadoResponseDto;
import edu.altair.ut5crm.entities.Estado;
import edu.altair.ut5crm.exceptions.DuplicateResourceException;
import edu.altair.ut5crm.exceptions.ResourceNotFoundException;
import edu.altair.ut5crm.repositories.EstadoRepository;
import edu.altair.ut5crm.services.EstadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EstadoServiceImpl implements EstadoService {

    private final EstadoRepository estadoRepository;

    public EstadoServiceImpl(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoResponseDto> obtenerTodos() {
        return estadoRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoResponseDto obtenerPorId(Long id) {
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado con ID: " + id));
        return mapToResponseDto(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoResponseDto obtenerPorNombre(String nombre) {
        Estado estado = estadoRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado con nombre: " + nombre));
        return mapToResponseDto(estado);
    }

    @Override
    @Transactional
    public EstadoResponseDto crear(EstadoRequestDto dto) {
        if (dto.nombre() != null && estadoRepository.existsByNombre(dto.nombre())) {
            throw new DuplicateResourceException("Ya existe un estado con el nombre: " + dto.nombre());
        }

        Estado estado = new Estado();
        estado.setNombre(dto.nombre());

        Estado guardado = estadoRepository.save(estado);
        return mapToResponseDto(guardado);
    }

    @Override
    @Transactional
    public EstadoResponseDto actualizar(Long id, EstadoRequestDto dto) {
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado con ID: " + id));

        if (dto.nombre() != null && !estado.getNombre().equalsIgnoreCase(dto.nombre()) && estadoRepository.existsByNombre(dto.nombre())) {
            throw new DuplicateResourceException("El nombre de estado " + dto.nombre() + " ya está en uso.");
        }

        estado.setNombre(dto.nombre());

        Estado actualizado = estadoRepository.save(estado);
        return mapToResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!estadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Imposible eliminar. Estado no encontrado con ID: " + id);
        }
        estadoRepository.deleteById(id);
    }

    private EstadoResponseDto mapToResponseDto(Estado estado) {
        Set<String> nombresAlumnos = estado.getAlumnos() != null
                ? estado.getAlumnos().stream()
                .map(a -> a.getNombre() + " " + a.getApellido())
                .collect(Collectors.toSet())
                : Set.of();

        return new EstadoResponseDto(
                estado.getIdEstado(),
                estado.getNombre(),
                nombresAlumnos
        );
    }
}