package com.proyud4.controller;

import com.proyud4.controller.util.paginator.PageRender;
import com.proyud4.model.entity.Cliente;
import com.proyud4.model.service.IClienteService;
import com.proyud4.model.service.IUploadFileService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;

/**
 * Controlador de la clase `Cliente`
 */
@Controller
@SessionAttributes("cliente")
public class ClienteController {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IUploadFileService uploadFileService;

    /**
     * Recupera la imagen asociada a los detalles del usuario que se está consultando.
     *
     * @param filename
     * @return
     */
    @Secured("ROLE_USER")
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
        Resource recurso;
        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    /**
     * Mapea los detalles del usuario solicitado a la ruta "/clientes/detalles/{id}".
     *
     * @param id    de usuario
     * @param model modelo de datos
     * @param flash aviso pesudo-pop-up de éxito
     * @return
     */
    @Secured("ROLE_USER")
    @GetMapping(value = "/clientes/detalles/{id}")
    public String ver(@PathVariable(name = "id") Long id, Model model, RedirectAttributes flash) {

        Cliente cliente = clienteService.fetchByIdWithFacturas(id);

        if (cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Detalles de: " + cliente.getNombre());

        return "detalleCliente";
    }

    /**
     * Mapea la lista de clientes al endpoint `/clientes`
     *
     * @param page  página a mostrar
     * @param model contenedor de datos
     * @return String de referencia de la vista `listaClientes`
     */
    @GetMapping({"/clientes", "/"})
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page,
                         Model model) {

        // Traemos de forma estática el contexto de seguridad para extraer los datos del usuario autentificado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            logger.info(String.format("Usuario autenticado: %s", authentication.getName()));
        }

        // Implementamos un sistema de paginación para que sea más visual:
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Cliente> clientes = clienteService.findAll(pageRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/clientes", clientes);

        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);

        return "listaClientes";
    }

    /**
     * Mapea la vista de formulario al endpoint `/clientes/nuevo`
     *
     * @param model contenedor de datos
     * @return String referencia de la vista formClientes
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/clientes/nuevo")
    public String crear(Model model) {

        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Formulario de cliente");

        return "formClientes";
    }

    /**
     * Mapea la contestación del formulario al endpoint `/clientes/nuevo`
     *
     * @param cliente objeto obtenido del formulario como parámetro
     * @param result  exito o fracaso de la validación del formulario
     * @param model   contenedor de datos
     * @param status  estado de la variable de sesión que debemos cerrar tras guardar
     * @return redirección al endpint `/clientes/nuevo`
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/clientes/nuevo")
    public String guardar(@Valid Cliente cliente,
                          BindingResult result,
                          Model model,
                          @RequestParam("file") MultipartFile foto,
                          RedirectAttributes flash,
                          SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de cliente");
            return "formClientes"; // Si hay errores, volvemos a la vista del formulario del cliente.
        }

        if (!foto.isEmpty()) {

            if (cliente.getId() != null
                    && cliente.getId() > 0
                    && cliente.getFoto() != null
                    && cliente.getFoto().length() > 0) {

                uploadFileService.delete(cliente.getFoto());
            }

            String uniqueFilename;
            try {
                uniqueFilename = uploadFileService.copy(foto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            flash.addFlashAttribute("info", "Imagen subida correctamente");

            cliente.setFoto(uniqueFilename);
        }

        clienteService.save(cliente); // Si no hay errores, persistimos el cliente en la BD.
        status.setComplete(); // completamos la sesión para la variable de sesión `cliente`.
        String msgFlash = (cliente.getId() != null) ? "Cliente editado con éxito!" : "Cliente creado con éxito!";
        flash.addFlashAttribute("success", msgFlash);

        return "redirect:/clientes";
    }

    /**
     * Edita un cliente existente en la base de datos. Para ello, lo envía como parámetro
     * al formulario, donde se puede hacer la edición de los datos.
     *
     * @param id    del cliente
     * @param model de datos
     * @return String referencia a la vista o redirección
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/clientes/editar/{id}")
    public String editar(@PathVariable(name = "id") Long id,
                         Model model,
                         RedirectAttributes flash) {

        Cliente cliente;
        if (id > 0) {
            cliente = clienteService.findById(id);
            if (cliente != null) {
                clienteService.save(cliente);
            } else {
                flash.addFlashAttribute("error", "El ID del cliente no existe en la base de datos!");
                return "redirect:/clientes";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("title", "Editar Cliente");

        return "formClientes";
    }

    /**
     * Elimina cliente pasado por id en el endpoint "/clientes/eliminar/{id}".
     *
     * @param id    de usuario
     * @param flash aviso pseudo-pop-up de éxito
     * @return redirección a "/clientes"
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/clientes/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id,
                           RedirectAttributes flash) {
        if (id > 0) {
            Cliente cliente = clienteService.findById(id);
            clienteService.deleteById(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito!");

            if (uploadFileService.delete(cliente.getFoto())) {
                flash.addFlashAttribute("info", "Foto: " + cliente.getFoto() + " eliminada con éxito!");
            }
        }

        return "redirect:/clientes";
    }

    /**
     * Verifica si el role del contexto es igual al role proporcionado por parámetro.
     * Este método es útil para manipular acciones de role de maenra programática.
     * @param role
     * @return si role de usuario en contexto coincide con role pasado por parametro
     */
    private boolean hasRole(String role) {

        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null) {
            return false;
        }

        Authentication authentication = context.getAuthentication();

        if (authentication == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return authorities.contains(new SimpleGrantedAuthority(role));
    }
}
