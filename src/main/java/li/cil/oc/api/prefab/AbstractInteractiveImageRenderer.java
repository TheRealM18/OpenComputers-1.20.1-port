package li.cil.oc.api.prefab;

import li.cil.oc.api.manual.InteractiveImageRenderer;

/**
 * Simple base implementation of {@link li.cil.oc.api.manual.InteractiveImageRenderer}.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractInteractiveImageRenderer implements InteractiveImageRenderer {
    @Override  CompletableFuture<String> getTooltipAsync(String tooltip) {
        return tooltip;
    }

    @Override  CompletableFuture<boolean> onMouseClickAsync(int mouseX, int mouseY) {
        return false;
    }
}
