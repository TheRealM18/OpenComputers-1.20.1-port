package li.cil.oc.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.MoreFiles;
import li.cil.oc.OpenComputers;
import li.cil.oc.api.detail.ManualAPI;
import li.cil.oc.api.manual.ContentProvider;
import li.cil.oc.api.manual.ImageProvider;
import li.cil.oc.api.manual.ImageRenderer;
import li.cil.oc.api.manual.PathProvider;
import li.cil.oc.api.manual.TabIconRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public final class Manual implements ManualAPI {
    public static final String LANGUAGE_KEY = "%LANGUAGE%";
    public static final String FALLBACK_LANGUAGE = "en_us";

    public static final Manual INSTANCE = new Manual();

    private final List<Tab> tabs = new ArrayList<>();
    private final List<PathProvider> pathProviders = new ArrayList<>();
    private final List<ContentProvider> contentProviders = new ArrayList<>();
    private final List<Pair<String, ImageProvider>> imageProviders = new ArrayList<>();
    private final Deque<History> history = new ArrayDeque<>();

    public static class History {
        public final String path;
        public int offset = 0;

        public History(String path) {
            this.path = path;
        }
    }

    public static class Tab {
        public final TabIconRenderer renderer;
        @Nullable
        public final String tooltip;
        public final String path;

        public Tab(TabIconRenderer renderer, @Nullable String tooltip, String path) {
            this.renderer = renderer;
            this.tooltip = tooltip;
            this.path = path;
        }
    }

    private Manual() {
        reset();
    }

    @Override
    public void addTab(TabIconRenderer renderer, String tooltip, String path) {
        tabs.add(new Tab(renderer, tooltip, path));
        if (tabs.size() > 7) {
            OpenComputers.log.warn("Gosh I'm popular! Too many tabs were added to the OpenComputers in-game manual, so some won't be shown. In case this actually happens, let me know and I'll look into making them scrollable or something...");
        }
    }

    @Override
    public void addProvider(PathProvider provider) {
        pathProviders.add(provider);
    }

    @Override
    public void addProvider(ContentProvider provider) {
        contentProviders.add(provider);
    }

    @Override
    public void addProvider(String prefix, ImageProvider provider) {
        imageProviders.add(Pair.of(Strings.isNullOrEmpty(prefix) ? "" : prefix + ":", provider));
    }

    @Override
    @Nullable
    public String pathFor(ItemStack stack) {
        for (PathProvider provider : pathProviders) {
            try {
                String path = provider.pathFor(stack);
                if (path != null) {
                    return path;
                }
            } catch (Throwable t) {
                OpenComputers.log.warn("A path provider threw an error when queried with an item.", t);
            }
        }
        return null;
    }

    @Override
    @Nullable
    public String pathFor(Level world, BlockPos pos) {
        for (PathProvider provider : pathProviders) {
            try {
                String path = provider.pathFor(world, pos);
                if (path != null) {
                    return path;
                }
            } catch (Throwable t) {
                OpenComputers.log.warn("A path provider threw an error when queried with a block.", t);
            }
        }
        return null;
    }

    @Override
    @Nullable
    public Iterable<String> contentFor(String path) {
        String cleanPath = MoreFiles.simplifyPath(Paths.get(path)).toString();
        String language = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        
        Iterable<String> content = contentForWithRedirects(cleanPath.replace(LANGUAGE_KEY, language), new ArrayList<>());
        if (content != null) {
            return content;
        }
        return contentForWithRedirects(cleanPath.replace(LANGUAGE_KEY, FALLBACK_LANGUAGE), new ArrayList<>());
    }

    @Override
    @Nullable
    public ImageRenderer imageFor(String href) {
        for (int i = imageProviders.size() - 1; i >= 0; i--) {
            Pair<String, ImageProvider> entry = imageProviders.get(i);
            if (href.startsWith(entry.getLeft())) {
                try {
                    ImageRenderer image = entry.getRight().getImage(href.substring(entry.getLeft().length()));
                    if (image != null) {
                        return image;
                    }
                } catch (Throwable t) {
                    OpenComputers.log.warn("An image provider threw an error when queried.", t);
                }
            }
        }
        return null;
    }

    @Override
    public void openFor(Player player) {
        if (player.level.isClientSide) {
            Minecraft mc = Minecraft.getInstance();
            if (player == mc.player) {
                // TODO: Push manual GUI
                // mc.pushGuiLayer(new gui.Manual());
            }
        }
    }

    public void reset() {
        history.clear();
        history.push(new History(LANGUAGE_KEY + "/index.md"));
    }

    @Override
    public void navigate(String path) {
        if (Minecraft.getInstance().screen instanceof gui.Manual) {
            // ((gui.Manual) Minecraft.getInstance().screen).pushPage(path);
        } else {
            history.push(new History(path));
        }
    }

    public String makeRelative(String path, String base) {
        if (path.startsWith("/")) {
            return path;
        }
        int lastSlash = base.lastIndexOf('/');
        return lastSlash >= 0 ? base.substring(0, lastSlash + 1) + path : path;
    }

    @Nullable
    private Iterable<String> contentForWithRedirects(String path, List<String> seen) {
        if (seen.contains(path)) {
            List<String> result = new ArrayList<>();
            result.add("Redirection loop: " + String.join(" -> ", seen) + " -> " + path);
            return result;
        }

        List<String> content = doContentLookup(path);
        if (content != null && !content.isEmpty()) {
            String firstLine = content.get(0);
            if (firstLine != null && firstLine.toLowerCase().startsWith("#redirect ")) {
                List<String> newSeen = new ArrayList<>(seen);
                newSeen.add(path);
                return contentForWithRedirects(
                    makeRelative(firstLine.substring("#redirect ".length()), path),
                    newSeen
                );
            }
            return content;
        }
        return null;
    }

    @Nullable
    private List<String> doContentLookup(String path) {
        for (ContentProvider provider : contentProviders) {
            try {
                Iterable<String> lines = provider.getContent(path);
                if (lines != null) {
                    List<String> result = new ArrayList<>();
                    lines.forEach(result::add);
                    return result;
                }
            } catch (Throwable t) {
                OpenComputers.log.warn("A content provider threw an error when queried.", t);
            }
        }
        return null;
    }
}
