package com.zapateria.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zapateria.models.Usuario;
import com.zapateria.repositories.UsuarioRepository;

import static com.zapateria.utils.Constants.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ========== AUTENTICACIÓN ==========

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario loginData) {
        Map<String, Object> response = new HashMap<>();

        if (loginData == null || loginData.getUsername() == null || loginData.getPassword() == null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, DATOS_INCOMPLETOS);
            return ResponseEntity.badRequest().body(response);
        }

        Usuario usuario = usuarioRepository.findByUsernameAndPassword(
                loginData.getUsername(), loginData.getPassword());

        if (usuario != null) {
            response.put(SUCCESS, true);
            response.put(MENSAJE, "Login exitoso");
            response.put("usuario", usuario);
            logger.info("Login exitoso para usuario: {}", loginData.getUsername());
        } else {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "Credenciales incorrectas");
        }
        return ResponseEntity.ok(response);
    }

    // ========== REGISTRO ==========

    @PostMapping("/registro-cliente")
    public ResponseEntity<Map<String, Object>> registroCliente(@RequestBody Usuario nuevoUsuario) {
        Map<String, Object> response = new HashMap<>();

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El usuario ya existe");
            return ResponseEntity.badRequest().body(response);
        }
        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()) != null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El email ya está registrado");
            return ResponseEntity.badRequest().body(response);
        }

        nuevoUsuario.setRol(ROL_CLIENTE);
        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Registro exitoso");
        response.put("usuario", guardado);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro-admin")
    public ResponseEntity<Map<String, Object>> registroAdmin(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        Usuario nuevoUsuario = construirUsuarioDesdeMap(data);
        nuevoUsuario.setRol(ROL_ADMINISTRADOR);

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El usuario ya existe");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Administrador registrado exitosamente");
        response.put("usuario", guardado);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro-empleado")
    public ResponseEntity<Map<String, Object>> registroEmpleado(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        Usuario nuevoUsuario = construirUsuarioDesdeMap(data);
        nuevoUsuario.setRol(ROL_EMPLEADO);

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El usuario ya existe");
            return ResponseEntity.badRequest().body(response);
        }
        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()) != null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El email ya está registrado");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Empleado registrado exitosamente");
        response.put("usuario", guardado);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro-despacho")
    public ResponseEntity<Map<String, Object>> registroDespacho(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        Usuario nuevoUsuario = construirUsuarioDesdeMap(data);
        nuevoUsuario.setRol(ROL_DESPACHO);

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El usuario ya existe");
            return ResponseEntity.badRequest().body(response);
        }
        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()) != null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El email ya está registrado");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Usuario de despacho registrado exitosamente");
        response.put("usuario", guardado);
        return ResponseEntity.ok(response);
    }

    // ========== ASIGNACIONES ==========

    @PostMapping("/asignar-cliente")
    public ResponseEntity<Map<String, Object>> asignarCliente(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        String clienteId  = data.get("clienteId");
        String empleadoId = data.get("empleadoId");

        if (clienteId == null || empleadoId == null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, DATOS_INCOMPLETOS);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Usuario> clienteOpt  = usuarioRepository.findById(clienteId);
        Optional<Usuario> empleadoOpt = usuarioRepository.findById(empleadoId);

        if (!clienteOpt.isPresent() || !empleadoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "Cliente o empleado no encontrado");
            return ResponseEntity.notFound().build();
        }

        Usuario cliente  = clienteOpt.get();
        Usuario empleado = empleadoOpt.get();

        // FIX: LiteralsFirstInComparisons — literal primero
        if (!ROL_EMPLEADO.equals(empleado.getRol())) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "El usuario seleccionado no es un empleado");
            return ResponseEntity.badRequest().body(response);
        }

        // Desasignar del empleado anterior si cambió
        String empleadoPrevioId = cliente.getEmpleadoAsignadoId();
        if (empleadoPrevioId != null && !empleadoPrevioId.equals(empleadoId)) {
            Optional<Usuario> empleadoAnteriorOpt = usuarioRepository.findById(empleadoPrevioId);
            if (empleadoAnteriorOpt.isPresent()) {
                Usuario empleadoAnterior = empleadoAnteriorOpt.get();
                List<String> cl = empleadoAnterior.getClientesAsignados();
                if (cl != null) { cl.remove(clienteId); empleadoAnterior.setClientesAsignados(cl); usuarioRepository.save(empleadoAnterior); }
            }
        }

        cliente.setEmpleadoAsignadoId(empleadoId);
        usuarioRepository.save(cliente);

        List<String> clientesEmpleado = empleado.getClientesAsignados();
        if (clientesEmpleado != null && !clientesEmpleado.contains(clienteId)) {
            clientesEmpleado.add(clienteId);
            empleado.setClientesAsignados(clientesEmpleado);
            usuarioRepository.save(empleado);
        }

        response.put(SUCCESS, true);
        response.put(MENSAJE, "Cliente asignado correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/desasignar-cliente")
    public ResponseEntity<Map<String, Object>> desasignarCliente(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        String clienteId = data.get("clienteId");

        if (clienteId == null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, ID_INVALIDO);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Usuario> clienteOpt = usuarioRepository.findById(clienteId);
        if (!clienteOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, USUARIO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }

        Usuario cliente    = clienteOpt.get();
        String  empleadoId = cliente.getEmpleadoAsignadoId();

        if (empleadoId != null) {
            Optional<Usuario> empleadoOpt = usuarioRepository.findById(empleadoId);
            if (empleadoOpt.isPresent()) {
                Usuario empleado = empleadoOpt.get();
                List<String> cl = empleado.getClientesAsignados();
                if (cl != null) { cl.remove(clienteId); empleado.setClientesAsignados(cl); usuarioRepository.save(empleado); }
            }
        }

        cliente.setEmpleadoAsignadoId(null);
        usuarioRepository.save(cliente);

        response.put(SUCCESS, true);
        response.put(MENSAJE, "Cliente desasignado correctamente");
        return ResponseEntity.ok(response);
    }

    // ========== CONSULTAS ==========

    @GetMapping("/empleados")
    public ResponseEntity<List<Usuario>> listarEmpleados() {
        return ResponseEntity.ok(usuarioRepository.findByRol(ROL_EMPLEADO));
    }

    @GetMapping("/empleado/{empleadoId}/clientes")
    public ResponseEntity<List<Usuario>> clientesDeEmpleado(@PathVariable String empleadoId) {
        if (empleadoId == null || empleadoId.trim().isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(usuarioRepository.findByEmpleadoAsignadoId(empleadoId));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) return ResponseEntity.badRequest().body(ID_INVALIDO);
        Optional<Usuario> opt = usuarioRepository.findById(id);
        return opt.isPresent() ? ResponseEntity.ok(opt.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/mis-clientes/{userId}")
    public ResponseEntity<List<Usuario>> misClientes(@PathVariable String userId) {
        Optional<Usuario> opt = usuarioRepository.findById(userId);
        if (!opt.isPresent()) return ResponseEntity.ok(new ArrayList<>());

        String rol = opt.get().getRol();
        // FIX: LiteralsFirstInComparisons — literal primero
        if (ROL_ADMINISTRADOR.equals(rol)) return ResponseEntity.ok(usuarioRepository.findByRol(ROL_CLIENTE));
        if (ROL_EMPLEADO.equals(rol))      return ResponseEntity.ok(usuarioRepository.findByEmpleadoAsignadoId(userId));
        return ResponseEntity.ok(new ArrayList<>());
    }

    // ========== MODIFICACIÓN / ELIMINACIÓN ==========

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        if (id == null || id.trim().isEmpty()) return ResponseEntity.badRequest().body(ID_INVALIDO);
        if (!usuarioRepository.existsById(id)) return ResponseEntity.notFound().build();
        usuario.setId(id);
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, ID_INVALIDO);
            return ResponseEntity.badRequest().body(response);
        }
        usuarioRepository.deleteById(id);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Usuario eliminado");
        return ResponseEntity.ok(response);
    }

    // ========== HELPER PRIVADO ==========

    private Usuario construirUsuarioDesdeMap(Map<String, String> data) {
        Usuario u = new Usuario();
        u.setNombre(data.get("nombre"));
        u.setApellido(data.get("apellido"));
        u.setEmail(data.get("email"));
        u.setUsername(data.get("username"));
        u.setPassword(data.get("password"));
        u.setTelefono(data.get("telefono"));
        return u;
    }
}
