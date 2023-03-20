package com.proyud4.controller;

import com.proyud4.model.entity.Cliente;
import com.proyud4.model.entity.Factura;
import com.proyud4.model.entity.ItemFactura;
import com.proyud4.model.entity.Producto;
import com.proyud4.model.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

    @Autowired
    private IClienteService clienteService;


    /**
     * Mapea el formulario al endpoint '/nuevo/{clienteId}'
     * @param clienteId número identificador del cliente a facturar
     * @param model contenedor de datos
     * @param flash aviso pseudo-popUp
     * @return devuelve la ruta del endpoint
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/nuevo/{clienteId}")
    public String crear(@PathVariable(value = "clienteId") Long clienteId,
                        Model model,
                        RedirectAttributes flash) {

        Cliente cliente = clienteService.findById(clienteId);

        if (cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
            return "redirect:/clientes";
        }

        Factura factura = new Factura();
        factura.setCliente(cliente);
        model.addAttribute("factura", factura);
        model.addAttribute("titulo", "Crear factura");

        return "factura/formFacturas";
    }


    /**
     * Genera un json con todos los productos para su correcto funcionamiento con jQuery
     * @param term nombre del producto
     * @return el json
     */
    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/cargar-productos/{term}", produces = {"application/json"})
    public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
        return clienteService.findByNombre(term);
    }


    /**
     * guarda una factura en un cliente
     * @param factura la porpia factura
     * @param result
     * @param model contenedor de datos
     * @param itemId lista de los ids de los productos
     * @param cantidad lista de las líneas de la factura
     * @param flash aviso pseudo-popUp
     * @param status estado de la sesión
     * @return devuelve los detalles del cliente con la nueva factura creada
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/nuevo")
    public String guardar(@Valid Factura factura,
                          BindingResult result,
                          Model model,
                          @RequestParam(name = "item_id[]", required = false) Long[] itemId,
                          @RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
                          RedirectAttributes flash,
                          SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Crear factura");
            return "factura/formFacturas";
        }

        if (itemId == null || itemId.length == 0) {
            model.addAttribute("titulo", "Crear factura");
            model.addAttribute("error", "No es posible crear una factura vacía");
            return "factura/formFacturas";
        }

        for (int i = 0; i < itemId.length; i++) {
            Producto producto = clienteService.findProductById(itemId[i]);

            ItemFactura linea = new ItemFactura();
            linea.setProducto(producto);
            linea.setCantidad(cantidad[i]);

            factura.addItemFactura(linea);
        }

        clienteService.saveFactura(factura);

        status.setComplete();

        flash.addFlashAttribute("success", "Factura craeda con éxito");

        return "redirect:/clientes/detalles/" + factura.getCliente().getId();
    }


    /**
     * Mapea los detalles de la factura solicitada a la ruta "/detalles/{id}".
     *
     * @param id    número identificador de factura
     * @param model modelo de datos
     * @param flash aviso pesudo-pop-up de éxito
     * @return devuelve la ruta de los detalles
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/detalles/{id}")
    public String ver(@PathVariable(name = "id") Long id,
                      Model model,
                      RedirectAttributes flash) {

        Factura factura = clienteService.fetchByIdWithClienteFacturaProducto(id);

        if (factura == null) {
            flash.addFlashAttribute("error", "La factura no existe en la base de datos");
            return "redirect:/clientes";
        }

        model.addAttribute("factura", factura);
        model.addAttribute("title", "Factura: ".concat(factura.getDescripcion()));

        return "factura/detalleFactura";
    }


    /**
     * Elimina la factura pasada por id en el endpoint "/eliminar/{id}".
     *
     * @param id    número identificador de usuario
     * @param flash aviso pseudo-pop-up de éxito
     * @return redirección a "/clientes"
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id,
                           RedirectAttributes flash) {

        Factura factura = clienteService.findFacturaById(id);

        if (factura != null) {
            clienteService.deleteFactura(id);
            flash.addFlashAttribute("success", "Factura eliminada con éxito");
            return "redirect:/clientes/detalles/" + factura.getCliente().getId();
        }

        flash.addFlashAttribute("error", "La factura no existe en la base de datos. No se pudo eliminar");
        return "redirect:/clientes";
    }
}
