package edu.altair.ut5crm.services.impl;

import edu.altair.ut5crm.dtos.TitulacionRequestDto;
import edu.altair.ut5crm.dtos.TitulacionResponseDto;
import edu.altair.ut5crm.entities.Titulacion;
import edu.altair.ut5crm.exceptions.DuplicateResourceException;
import edu.altair.ut5crm.exceptions.ResourceNotFoundException;
import edu.altair.ut5crm.repositories.TitulacionRepository;
import edu.altair.ut5crm.services.TitulacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TitulacionServiceImpl implements TitulacionService {

    private final TitulacionRepository titulacionRepository;

    public TitulacionServiceImpl(TitulacionRepository titulacionRepository) {
        this.titulacionRepository = titulacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TitulacionResponseDto> obtenerTodas() {
        return titulacionRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TitulacionResponseDto obtenerPorId(Long id) {
        Titulacion titulacion = titulacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la titulación con ID: " + id));
        return mapToResponseDto(titulacion);
    }

    @Override
    @Transactional(readOnly = true)
    public TitulacionResponseDto obtenerPorCodigo(String codigo) {
        Titulacion titulacion = titulacionRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la titulación con código: " + codigo));
        return mapToResponseDto(titulacion);
    }

    @Override
    @Transactional(readOnly = true)
    public TitulacionResponseDto obtenerPorDiminutivo(String diminutivo) {
        Titulacion titulacion = titulacionRepository.findByDiminutivo(diminutivo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la titulación con diminutivo: " + diminutivo));
        return mapToResponseDto(titulacion);
    }

    @Override
    @Transactional
    public TitulacionResponseDto crear(TitulacionRequestDto dto) {
        if (dto.codigo() != null && titulacionRepository.existsByCodigo(dto.codigo())) {
            throw new DuplicateResourceException("Ya existe una titulación con el código: " + dto.codigo());
        }
        if (dto.diminutivo() != null && titulacionRepository.existsByDiminutivo(dto.diminutivo())) {
            throw new DuplicateResourceException("Ya existe una titulación con el diminutivo: " + dto.diminutivo());
        }

        Titulacion titulacion = new Titulacion();
        titulacion.setCodigo(dto.codigo());
        titulacion.setDiminutivo(dto.diminutivo());
        titulacion.setHorasTotales(dto.horasTotales());

        Titulacion guardada = titulacionRepository.save(titulacion);
        return mapToResponseDto(guardada);
    }

    @Override
    @Transactional
    public TitulacionResponseDto actualizar(Long id, TitulacionRequestDto dto) {
        Titulacion titulacion = titulacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la titulación con ID: " + id));

        if (dto.codigo() != null && !titulacion.getCodigo().equalsIgnoreCase(dto.codigo()) && titulacionRepository.existsByCodigo(dto.codigo())) {
            throw new DuplicateResourceException("El código " + dto.codigo() + " ya está en uso por otra titulación.");
        }
        if (dto.diminutivo() != null && !titulacion.getDiminutivo().equalsIgnoreCase(dto.diminutivo()) && titulacionRepository.existsByDiminutivo(dto.diminutivo())) {
            throw new DuplicateResourceException("El diminutivo " + dto.diminutivo() + " ya está en uso por otra titulación.");
        }

        titulacion.setCodigo(dto.codigo());
        titulacion.setDiminutivo(dto.diminutivo());
        titulacion.setHorasTotales(dto.horasTotales());

        Titulacion actualizada = titulacionRepository.save(titulacion);
        return mapToResponseDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!titulacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Imposible eliminar. Titulación no encontrada con ID: " + id);
        }
        titulacionRepository.deleteById(id);
    }

    private TitulacionResponseDto mapToResponseDto(Titulacion titulacion) {
        Set<String> diminutivosCursos = titulacion.getCursos() != null
                ? titulacion.getCursos().stream()
                .map(c -> c.getDiminutivo() + " - " + c.getDescripcion())
                .collect(Collectors.toSet())
                : Set.of();

        return new TitulacionResponseDto(
                titulacion.getIdTitulacion(),
                titulacion.getCodigo(),
                titulacion.getDiminutivo(),
                titulacion.getHorasTotales(),
                diminutivosCursos
        );
    }
}