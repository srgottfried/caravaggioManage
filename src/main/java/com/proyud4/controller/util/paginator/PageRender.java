package com.proyud4.controller.util.paginator;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para paginar contenido genérico.
 * @param <T>
 */
public class PageRender<T> {

    private String url;
    private Page<T> page;
    private int totalPaginas;
    private int numElemPorPagina;
    private int paginaActual;

    private List<PageItem> paginas;

    public PageRender(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.paginas = new ArrayList<>();

        this.numElemPorPagina = page.getSize();
        this.totalPaginas = page.getTotalPages();
        this.paginaActual = page.getNumber() + 1;

        int desde, hasta;
        if (totalPaginas <= numElemPorPagina) {
            desde = 1;
            hasta = totalPaginas;
        } else if (paginaActual <= numElemPorPagina / 2) {
            desde = 1;
            hasta = numElemPorPagina;
        } else if (paginaActual >= totalPaginas - numElemPorPagina / 2) {
            desde = totalPaginas - numElemPorPagina + 1;
            hasta = numElemPorPagina;
        } else {
            desde = paginaActual - numElemPorPagina / 2;
            hasta = numElemPorPagina;
        }

        for (int i = 0; i < hasta; i++) {
            paginas.add(new PageItem(desde + i, paginaActual == desde + i));
        }
    }

    public String getUrl() {
        return url;
    }

    public Page<T> getPage() {
        return page;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public int getNumElemPorPagina() {
        return numElemPorPagina;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public List<PageItem> getPaginas() {
        return paginas;
    }

    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean hasNext() {
        return page.hasNext();
    }

    public boolean hasPrevious() {
        return page.hasPrevious();
    }
}
