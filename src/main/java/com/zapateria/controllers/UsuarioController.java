package com.zapateria.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zapateria.models.Usuario;
import com.zapateria.repositories.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

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
            }
        }

        cliente.setEmpleadoAsignadoId(null);
        usuarioRepository.save(cliente);

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
}