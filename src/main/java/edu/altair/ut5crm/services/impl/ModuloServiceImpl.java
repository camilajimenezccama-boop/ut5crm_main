package edu.altair.ut5crm.services.impl;

import edu.altair.ut5crm.dtos.ModuloRequestDto;
import edu.altair.ut5crm.dtos.ModuloResponseDto;
import edu.altair.ut5crm.entities.Modulo;
import edu.altair.ut5crm.exceptions.DuplicateResourceException;
import edu.altair.ut5crm.exceptions.ResourceNotFoundException;
import edu.altair.ut5crm.repositories.ModuloRepository;
import edu.altair.ut5crm.services.ModuloService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModuloServiceImpl implements ModuloService {

    private final ModuloRepository moduloRepository;

    public ModuloServiceImpl(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuloResponseDto> obtenerTodos() {
        return moduloRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloResponseDto obtenerPorId(Long id) {
        Modulo modulo = moduloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el módulo con ID: " + id));
        return mapToResponseDto(modulo);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloResponseDto obtenerPorCodigo(String codigo) {
        Modulo modulo = moduloRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el módulo con código: " + codigo));
        return mapToResponseDto(modulo);
    }

    @Override
    @Transactional
    public ModuloResponseDto crear(ModuloRequestDto dto) {
        if (dto.codigo() != null && moduloRepository.existsByCodigo(dto.codigo())) {
            throw new DuplicateResourceException("Ya existe un módulo con el código: " + dto.codigo());
        }
        if (dto.diminutivo() != null && moduloRepository.existsByDiminutivo(dto.diminutivo())) {
            throw new DuplicateResourceException("Ya existe un módulo con el diminutivo: " + dto.diminutivo());
        }

        Modulo modulo = new Modulo();
        modulo.setCodigo(dto.codigo());
        modulo.setDiminutivo(dto.diminutivo());
        modulo.setNombre(dto.nombre());
        modulo.setDescripcion(dto.descripcion());
        modulo.setHorasSemanales(dto.horasSemanales());
        modulo.setCreditos(dto.creditos());

        Modulo guardado = moduloRepository.save(modulo);
        return mapToResponseDto(guardado);
    }

    @Override
    @Transactional
    public ModuloResponseDto actualizar(Long id, ModuloRequestDto dto) {
        Modulo modulo = moduloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el módulo con ID: " + id));

        if (dto.codigo() != null && !modulo.getCodigo().equalsIgnoreCase(dto.codigo()) && moduloRepository.existsByCodigo(dto.codigo())) {
            throw new DuplicateResourceException("El código " + dto.codigo() + " ya está en uso por otro módulo.");
        }
        if (dto.diminutivo() != null && !modulo.getDiminutivo().equalsIgnoreCase(dto.diminutivo()) && moduloRepository.existsByDiminutivo(dto.diminutivo())) {
            throw new DuplicateResourceException("El diminutivo " + dto.diminutivo() + " ya está en uso por otro módulo.");
        }

        modulo.setCodigo(dto.codigo());
        modulo.setDiminutivo(dto.diminutivo());
        modulo.setNombre(dto.nombre());
        modulo.setDescripcion(dto.descripcion());
        modulo.setHorasSemanales(dto.horasSemanales());
        modulo.setCreditos(dto.creditos());

        Modulo actualizado = moduloRepository.save(modulo);
        return mapToResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!moduloRepository.existsById(id)) {
            throw new ResourceNotFoundException("Imposible eliminar. Módulo no encontrado con ID: " + id);
        }
        moduloRepository.deleteById(id);
    }

    private ModuloResponseDto mapToResponseDto(Modulo modulo) {
        Set<String> nombresAlumnos = modulo.getAlumnos() != null
                ? modulo.getAlumnos().stream()
                .map(a -> a.getNombre() + " " + a.getApellido())
                .collect(Collectors.toSet())
                : Set.of();

        return new ModuloResponseDto(
                modulo.getIdModulo(),
                modulo.getCodigo(),
                modulo.getDiminutivo(),
                modulo.getNombre(),
                modulo.getDescripcion(),
                modulo.getHorasSemanales(),
                modulo.getCreditos(),
                nombresAlumnos
        );
    }
}