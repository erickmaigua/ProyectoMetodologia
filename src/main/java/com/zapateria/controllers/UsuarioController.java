package com.zapateria.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
=======

import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======

import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
import org.springframework.web.bind.annotation.*;

import com.zapateria.models.Usuario;
import com.zapateria.repositories.UsuarioRepository;

<<<<<<< HEAD
<<<<<<< HEAD
import static com.zapateria.utils.Constants.*;

=======
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

<<<<<<< HEAD
<<<<<<< HEAD
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
=======
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Usuario loginData) {
        Map<String, Object> response = new HashMap<>();

        Usuario usuario = usuarioRepository.findByUsernameAndPassword(
                loginData.getUsername(),
                loginData.getPassword());

        if (usuario != null) {
            response.put("success", true);
            response.put("mensaje", "Login exitoso");
            response.put("usuario", usuario);
        } else {
            response.put("success", false);
            response.put("mensaje", "Credenciales incorrectas");
        }

        return response;
    }

    // Registro de clientes
    @PostMapping("/registro-cliente")
    public Map<String, Object> registroCliente(@RequestBody Usuario nuevoUsuario) {
        Map<String, Object> response = new HashMap<>();

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put("success", false);
            response.put("mensaje", "El usuario ya existe");
            return response;
        }

        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()) != null) {
            response.put("success", false);
            response.put("mensaje", "El email ya está registrado");
            return response;
        }

        nuevoUsuario.setRol("CLIENTE");
        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        response.put("success", true);
        response.put("mensaje", "Registro exitoso");
        response.put("usuario", guardado);

        return response;
    }

    // Registro de administradores
    @PostMapping("/registro-admin")
    public Map<String, Object> registroAdmin(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(data.get("nombre"));
        nuevoUsuario.setApellido(data.get("apellido"));
        nuevoUsuario.setEmail(data.get("email"));
        nuevoUsuario.setUsername(data.get("username"));
        nuevoUsuario.setPassword(data.get("password"));
        nuevoUsuario.setRol("ADMINISTRADOR");

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put("success", false);
            response.put("mensaje", "El usuario ya existe");
            return response;
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        response.put("success", true);
        response.put("mensaje", "Administrador registrado exitosamente");
        response.put("usuario", guardado);

        return response;
    }

    // Registro de empleados
    @PostMapping("/registro-empleado")
    public Map<String, Object> registroEmpleado(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(data.get("nombre"));
        nuevoUsuario.setApellido(data.get("apellido"));
        nuevoUsuario.setEmail(data.get("email"));
        nuevoUsuario.setUsername(data.get("username"));
        nuevoUsuario.setPassword(data.get("password"));
        nuevoUsuario.setTelefono(data.get("telefono"));
        nuevoUsuario.setRol("EMPLEADO");

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put("success", false);
            response.put("mensaje", "El usuario ya existe");
            return response;
        }

        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()) != null) {
            response.put("success", false);
            response.put("mensaje", "El email ya está registrado");
            return response;
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        response.put("success", true);
        response.put("mensaje", "Empleado registrado exitosamente");
        response.put("usuario", guardado);

        return response;
    }

    // Registro de usuarios de despacho
    @PostMapping("/registro-despacho")
    public Map<String, Object> registroDespacho(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(data.get("nombre"));
        nuevoUsuario.setApellido(data.get("apellido"));
        nuevoUsuario.setEmail(data.get("email"));
        nuevoUsuario.setUsername(data.get("username"));
        nuevoUsuario.setPassword(data.get("password"));
        nuevoUsuario.setTelefono(data.get("telefono"));
        nuevoUsuario.setRol("DESPACHO");

        if (usuarioRepository.findByUsername(nuevoUsuario.getUsername()) != null) {
            response.put("success", false);
            response.put("mensaje", "El usuario ya existe");
            return response;
        }

        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()) != null) {
            response.put("success", false);
            response.put("mensaje", "El email ya está registrado");
            return response;
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        response.put("success", true);
        response.put("mensaje", "Usuario de despacho registrado exitosamente");
        response.put("usuario", guardado);

        return response;
    }

    @PostMapping("/asignar-cliente")
    public Map<String, Object> asignarCliente(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        String clienteId = data.get("clienteId");
        String empleadoId = data.get("empleadoId");

        Usuario cliente = usuarioRepository.findById(clienteId).orElse(null);
        Usuario empleado = usuarioRepository.findById(empleadoId).orElse(null);

        if (cliente == null || empleado == null) {
            response.put("success", false);
            response.put("mensaje", "Cliente o empleado no encontrado");
            return response;
        }

        if (!empleado.getRol().equals("EMPLEADO")) {
            response.put("success", false);
            response.put("mensaje", "El usuario seleccionado no es un empleado");
            return response;
        }

        // Validar que el cliente no tenga ya un empleado asignado
        if (cliente.getEmpleadoAsignadoId() != null && !cliente.getEmpleadoAsignadoId().equals(empleadoId)) {
            // Desasignar del empleado anterior
            Usuario empleadoAnterior = usuarioRepository.findById(cliente.getEmpleadoAsignadoId()).orElse(null);
            if (empleadoAnterior != null) {
                empleadoAnterior.getClientesAsignados().remove(clienteId);
                usuarioRepository.save(empleadoAnterior);
            }
        }

        // Actualizar cliente con empleado asignado
        cliente.setEmpleadoAsignadoId(empleadoId);
        usuarioRepository.save(cliente);

        // Agregar cliente a la lista del empleado (si no existe)
        if (!empleado.getClientesAsignados().contains(clienteId)) {
            empleado.getClientesAsignados().add(clienteId);
            usuarioRepository.save(empleado);
        }

        response.put("success", true);
        response.put("mensaje", "Cliente asignado correctamente");

        return response;
    }

    // Desasignar cliente de empleado
    @PostMapping("/desasignar-cliente")
    public Map<String, Object> desasignarCliente(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        String clienteId = data.get("clienteId");

        Usuario cliente = usuarioRepository.findById(clienteId).orElse(null);

        if (cliente == null) {
            response.put("success", false);
            response.put("mensaje", "Cliente no encontrado");
            return response;
        }

        String empleadoId = cliente.getEmpleadoAsignadoId();

        if (empleadoId != null) {
            Usuario empleado = usuarioRepository.findById(empleadoId).orElse(null);
            if (empleado != null) {
                empleado.getClientesAsignados().remove(clienteId);
                usuarioRepository.save(empleado);
<<<<<<< HEAD
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
            }
        }

        cliente.setEmpleadoAsignadoId(null);
        usuarioRepository.save(cliente);

<<<<<<< HEAD
<<<<<<< HEAD
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
=======
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
        response.put("success", true);
        response.put("mensaje", "Cliente desasignado correctamente");

        return response;
    }

    // Obtener empleados
    @GetMapping("/empleados")
    public List<Usuario> listarEmpleados() {
        return usuarioRepository.findByRol("EMPLEADO");
    }

    // Obtener clientes asignados a un empleado
    @GetMapping("/empleado/{empleadoId}/clientes")
    public List<Usuario> clientesDeEmpleado(@PathVariable String empleadoId) {
        return usuarioRepository.findByEmpleadoAsignadoId(empleadoId);
    }

    // Listar todos los usuarios
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public Usuario obtenerUsuario(@PathVariable String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public Map<String, Object> eliminarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        usuarioRepository.deleteById(id);
        response.put("success", true);
        response.put("mensaje", "Usuario eliminado");
        return response;
    }

    // Obtener todos los clientes (admin) o solo los del empleado
    @GetMapping("/mis-clientes/{userId}")
    public List<Usuario> misClientes(@PathVariable String userId) {
        Usuario usuario = usuarioRepository.findById(userId).orElse(null);

        if (usuario == null) {
            return new ArrayList<>();
        }

        if ("ADMINISTRADOR".equals(usuario.getRol())) {
            return usuarioRepository.findByRol("CLIENTE");
        }

        if ("EMPLEADO".equals(usuario.getRol())) {
            return usuarioRepository.findByEmpleadoAsignadoId(userId);
        }

        return new ArrayList<>();
    }
<<<<<<< HEAD
}
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
}
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
