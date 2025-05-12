package com.torneados.web.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.torneados.web.entities.Equipo;
import com.torneados.web.entities.Usuario;
import com.torneados.web.exceptions.ResourceNotFoundException;
import com.torneados.web.exceptions.UnauthorizedException;
import com.torneados.web.exceptions.AccessDeniedException;
import com.torneados.web.repositories.EquipoRepository;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final AuthService authService;
    private final UploadService uploadService;

    public EquipoService(EquipoRepository equipoRepository, AuthService authService, UploadService uploadService) {
        this.uploadService = uploadService;
        this.equipoRepository = equipoRepository;
        this.authService = authService;
    }

    public Equipo createEquipo(Equipo equipo, MultipartFile archivo) throws IOException {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        equipo.setCreador(currentUser);
        Equipo equipoGuardado = equipoRepository.save(equipo);

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = uploadService.guardarImagen(archivo, "equipo_" + equipoGuardado.getIdEquipo());
            equipoGuardado.setLogoUrl(urlImagen);
        } else {
            equipoGuardado.setLogoUrl("/uploads/default-team-logo.png");
        }

        return equipoRepository.save(equipoGuardado);
    }

    public Equipo getEquipoById(Long id) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && (equipo.getCreador() == null || !equipo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario()))) {
            throw new AccessDeniedException("Sin permisos para ver este equipo");
        }

        return equipo;
    }

    public Equipo updateEquipo(Long idEquipo, String nombre, MultipartFile logo) throws IOException {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Falta autenticación");
        }

        Equipo equipoExistente = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !equipoExistente.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para actualizar este equipo");
        }

        equipoExistente.setNombre(nombre);

        if (logo != null && !logo.isEmpty()) {
            String urlImagen = uploadService.guardarImagen(logo, "equipo_" + idEquipo);
            equipoExistente.setLogoUrl(urlImagen);
        }

        return equipoRepository.save(equipoExistente);
    }

    public void deleteEquipo(Long idEquipo) {
        Usuario currentUser = authService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Falta autenticación");
        }

        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + idEquipo));

        if (!currentUser.getRol().equals(Usuario.Rol.ADMINISTRADOR)
                && !equipo.getCreador().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("Sin permisos para eliminar este equipo");
        }

        equipoRepository.delete(equipo);
    }
}
