package com.hbm.ntm.common.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("null")
public abstract class MachineRecipeRegistry<R> {
    private final List<R> recipes = new ArrayList<>();
    private boolean initialized;

    public final List<R> all() {
        this.ensureInitialized();
        return List.copyOf(this.recipes);
    }

    public final Optional<R> findFirst(final Predicate<R> predicate) {
        this.ensureInitialized();
        return this.recipes.stream().filter(predicate).findFirst();
    }

    protected final void addRecipe(final R recipe) {
        this.recipes.add(recipe);
    }

    protected final void addAllRecipes(final List<R> recipes) {
        this.recipes.addAll(recipes);
    }

    protected final void clearRecipes() {
        this.recipes.clear();
    }

    private synchronized void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.registerDefaults();
    }

    protected abstract void registerDefaults();
}
