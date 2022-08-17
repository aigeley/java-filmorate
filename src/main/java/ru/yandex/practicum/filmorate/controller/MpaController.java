package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.controller.MpaController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public class MpaController {
    public static final String BASE_PATH = "/mpa";
    private final MpaService mpaService;

    protected MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public Mpa get(@PathVariable("id") int mpaId) {
        return mpaService.get(mpaId);
    }

    @GetMapping
    public Collection<Mpa> getAll() {
        return mpaService.getAll();
    }
}
