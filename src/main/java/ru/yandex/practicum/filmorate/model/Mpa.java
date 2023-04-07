package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

@Getter
public class Mpa implements Comparable<Mpa> {

    public static final Mpa G       = new Mpa(1L, "G");
    public static final Mpa PG      = new Mpa(2L, "PG");
    public static final Mpa PG_13   = new Mpa(3L, "PG-13");
    public static final Mpa R       = new Mpa(4L, "R");
    public static final Mpa NC_17   = new Mpa(5L, "NC-17");

    private final Long id;
    private final String name;

    private Mpa(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Mpa getMpa(Long id) {

        if (id == 1) {
            return G;
        } else if (id == 2) {
            return PG;
        } else if (id == 3) {
            return PG_13;
        } else if (id == 4) {
            return R;
        } else if (id == 5) {
            return NC_17;
        } else {
            return null;
        }

    }

    public static Mpa getMpa(Mpa mpa) {

        if (mpa == null) {
            return null;
        } else if (mpa.id == 1) {
            return G;
        } else if (mpa.id == 2) {
            return PG;
        } else if (mpa.id == 3) {
            return PG_13;
        } else if (mpa.id == 4) {
            return R;
        } else if (mpa.id == 5) {
            return NC_17;
        } else {
            return null;
        }

    }

    public static Set<Mpa> getMpas() {

        Set<Mpa> res = new TreeSet<>();
        res.add(G);
        res.add(PG);
        res.add(PG_13);
        res.add(R);
        res.add(NC_17);
        return res;
    }

    @Override
    public int compareTo(@NotNull Mpa other) {
        return Long.compare(this.id, other.id);
    }

}