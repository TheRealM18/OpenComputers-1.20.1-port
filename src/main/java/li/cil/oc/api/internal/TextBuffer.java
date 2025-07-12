package li.cil.oc.api.internal;

import com.mojang.blaze3d.matrix.MatrixStack;
import li.cil.oc.api.Persistable;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This interface implements functionality for displaying and manipulating
 * text, like screens and robots. An implementation can be obtained via the
 * screens' item driver.
 * <p/>
 * This allows re-using the built-in screen logic in third-party code without
 * access to the internals of OC.
 * <p/>
 * To get an instance of the buffer component, use its item driver, e.g.:
 * <pre>
 *     final ItemStack stack = li.cil.oc.api.Items.get("screen1").createItemStack(1);
 *     final TextBuffer buffer = (TextBuffer) li.cil.oc.api.Driver.driverFor(stack).createEnvironment(stack, this);
 * </pre>
 */
public interface TextBuffer extends ManagedEnvironment, Persistable {
    /**
     * Controls how much energy the buffer will consume per tick.
     * <p/>
     * This is <em>not</em> necessarily the actual amount consumed per tick,
     * instead it is a base value that cost is based on, incorporating a few
     * other factors. This is the cost a tier one screen will consume if every
     * character  CompletableFuture<is> litAsync(non-black).  CompletableFuture<Larger> buffersAsync(i.e. buffers with a higher
     * maximum resolution) cost proportionally more.
     * <p/>
     * Note that this amount of energy is not necessarily subtracted each tick,
     * instead every n ticks, n times the amount of energy it costs to run the
     * buffer will be consumed, where n is configurable in the OC config.
     * <p/>
     * This defaults to OC's built-in default value.
     *
     * @param value the base energy cost per tick.
     * @see #getEnergyCostPerTick()
     */  CompletableFuture<Void> setEnergyCostPerTickAsync(double value);

    /**
     * Get the energy cost per tick.
     *
     * @return the base energy cost per tick.
     * @see #setEnergyCostPerTick(double)
     */  CompletableFuture<double> getEnergyCostPerTickAsync();

    /**
     * Set whether the buffer is powered on.
     * <p/>
     * For example, screens can be powered on and off by sending a redstone
     * pulse into them, in addition to their component API.
     *
     * @param value whether the buffer should be on or not.
     * @see #getPowerState()
     */  CompletableFuture<Void> setPowerStateAsync(boolean value);

    /**
     * Get the current power state.
     *
     * @return whether the buffer is powered on.
     * @see #setPowerState(boolean)
     */  CompletableFuture<boolean> getPowerStateAsync();

    /**
     * Sets the maximum resolution supported by this buffer.
     *
     * @param width  the maximum horizontal resolution, in characters.
     * @param height the maximum vertical resolution, in characters.
     */  CompletableFuture<Void> setMaximumResolutionAsync(int width, int height);

    /**
     * Get the maximum horizontal size of the buffer.
     */  CompletableFuture<int> getMaximumWidthAsync();

    /**
     * Get the maximum vertical size of the buffer.
     */  CompletableFuture<int> getMaximumHeightAsync();

    /**
     * Set the 'aspect ratio' of the buffer.
     * <p/>
     * Not to be confused with the maximum resolution of the buffer, this
     * refers to the 'physical' size of the buffer's container. For multi-
     * block screens, for example, this is the number of horizontal and
     * vertical blocks.
     *
     * @param width  the horizontal size of the physical representation.
     * @param height the vertical size of the physical representation.
     */  CompletableFuture<Void> setAspectRatioAsync(double width, double height);

    /**
     * Get the aspect ratio of the buffer.
     * <p/>
     * Note that this is in fact <tt>width / height</tt>.
     *
     * @see #setAspectRatio(double, double)
     */  CompletableFuture<double> getAspectRatioAsync();

    /**
     * Set the buffer's active resolution.
     *
     * @param width  the horizontal resolution.
     * @param height the vertical resolution.
     * @return <tt>true</tt> if the resolution changed.
     */  CompletableFuture<boolean> setResolutionAsync(int width, int height);

    /**
     * Get the current horizontal resolution.
     *
     * @see #setResolution(int, int)
     */  CompletableFuture<int> getWidthAsync();

    /**
     * Get the current vertical resolution.
     *
     * @see #setResolution(int, int)
     */  CompletableFuture<int> getHeightAsync();

    /**
     * Set the buffer's active viewport resolution.
     * <p/>
     * This cannot exceed the current buffer resolution.
     *
     * @param width  the horizontal resolution.
     * @param height the vertical resolution.
     * @return <tt>true</tt> if the resolution changed.
     * @see #setResolution(int, int)
     */  CompletableFuture<boolean> setViewportAsync(int width, int height);

    /**
     * Get the current horizontal viewport resolution.
     *
     * @see #setViewport(int, int)
     */  CompletableFuture<int> getViewportWidthAsync();

    /**
     * Get the current vertical viewport resolution.
     *
     * @see #setViewport(int, int)
     */  CompletableFuture<int> getViewportHeightAsync();

    /**
     * Sets the maximum color depth supported by this buffer.
     * <p/>
     * Note that this is the <em>maximum</em> supported depth, lower depths
     * will be supported, too. So when setting this to four bit, one bit will
     * be supported, too. When setting this to eight bit, four and one bit
     * will be supported, also.
     *
     * @param depth the maximum color depth of the buffer.
     */  CompletableFuture<Void> setMaximumColorDepthAsync(ColorDepth depth);

    /**
     * Get the maximum color depth supported.
     */  CompletableFuture<ColorDepth> getMaximumColorDepthAsync();

    /**
     * Set the active color depth for this buffer.
     *
     * @param depth the new color depth.
     * @return <tt>true</tt> if the color depth changed.
     */  CompletableFuture<boolean> setColorDepthAsync(ColorDepth depth);

    /**
     * Get the active color depth of this buffer.
     */  CompletableFuture<ColorDepth> getColorDepthAsync();

    /**
     * Set the color of the active color palette at the specified index.
     * <p/>
     * This will error if the current depth does not have  CompletableFuture<a> paletteAsync(one bit).
     *
     * @param index the index at which to set the color.
     * @param color the color to set for the specified index.
     */  CompletableFuture<Void> setPaletteColorAsync(int index, int color);

    /**
     * Get the color in the active color palette at the specified index.
     * <p/>
     * This will error if the current depth does not have  CompletableFuture<a> paletteAsync(one bit).
     *
     * @param index the index at which to get the color.
     * @return the color in the active palette at the specified index.
     */  CompletableFuture<int> getPaletteColorAsync(int index);

    /**
     * Set the active foreground color, not using a palette.
     *
     * @param color the new foreground color.
     * @see #setForegroundColor(int, boolean)
     */  CompletableFuture<Void> setForegroundColorAsync(int color);

    /**
     * Set the active foreground color.
     * <p/>
     * If the value is not from the palette, the actually stored value may
     * differ from the specified one, as it is converted to the buffer's
     * current color depth.
     * <p/>
     * For palette-only  CompletableFuture<color> formatsAsync(four bit) the best fit from the palette
     * is chosen, if the value is not from the palette.
     *
     * @param color         the color or palette index.
     * @param isFromPalette <tt>true</tt>if <tt>color</tt> specifies a palette index.
     */  CompletableFuture<Void> setForegroundColorAsync(int color, boolean isFromPalette);

    /**
     * The active foreground color.
     */  CompletableFuture<int> getForegroundColorAsync();

    /**
     * <tt>true</tt> if the foreground color is from the color palette, meaning
     * the value returned from {@link #getForegroundColor()} is the color
     * palette index.
     */  CompletableFuture<boolean> isForegroundFromPaletteAsync();

    /**
     * Set the active background color, not using a palette.
     *
     * @param color the new background color.
     * @see #setBackgroundColor(int, boolean)
     */  CompletableFuture<Void> setBackgroundColorAsync(int color);

    /**
     * Set the active background color.
     * <p/>
     * If the value is not from the palette, the actually stored value may
     * differ from the specified one, as it is converted to the buffer's
     * current color depth.
     * <p/>
     * For palette-only  CompletableFuture<color> formatsAsync(four bit) the best fit from the palette
     * is chosen, if the value is not from the palette.
     *
     * @param color         the color or palette index.
     * @param isFromPalette <tt>true</tt>if <tt>color</tt> specifies a palette index.
     */  CompletableFuture<Void> setBackgroundColorAsync(int color, boolean isFromPalette);

    /**
     * The active background color.
     */  CompletableFuture<int> getBackgroundColorAsync();

    /**
     * <tt>true</tt> if the background color is from the color palette, meaning
     * the value returned from {@link #getBackgroundColor()} is the color
     * palette index.
     */  CompletableFuture<boolean> isBackgroundFromPaletteAsync();

    /**
     * Copy a portion of the text buffer.
     * <p/>
     * This will copy the area's text and colors.
     *
     * @param column                the starting horizontal index of the area to copy.
     * @param row                   the starting vertical index of the area to copy.
     * @param width                 the width of the area to copy.
     * @param height                the height of the area to copy.
     * @param horizontalTranslation the horizontal offset, relative to the starting column to copy the are to.
     * @param verticalTranslation   the vertical offset, relative to the starting row to copy the are to.
     */  CompletableFuture<Void> copyAsync(int column, int row, int width, int height, int horizontalTranslation, int verticalTranslation);

    /**
     * Fill a portion of the text buffer.
     * <p/>
     * This will set the area's colors to the currently active ones.
     *
     * @param column the starting horizontal index of the area to fill.
     * @param row    the starting vertical index of the area to fill.
     * @param width  the width of the area to fill.
     * @param height the height of the area to fill.
     * @param value  the character to fill the area with.
     */  CompletableFuture<Void> fillAsync(int column, int row, int width, int height, char value);

    /**
     * Write a string into the text buffer.
     * <p/>
     * This will apply the currently active colors to the changed area.
     *
     * @param column   the starting horizontal index to write at.
     * @param row      the starting vertical index to write at.
     * @param value    the string to write.
     * @param vertical <tt>true</tt> if the string should be written vertically instead of horizontally.
     */  CompletableFuture<Void> setAsync(int column, int row, String value, boolean vertical);

    /**
     * Get the character in the text buffer at the specified location.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @return the character at that index.
     */  CompletableFuture<char> getAsync(int column, int row);

    /**
     * Get the foreground color of the text buffer at the specified location.
     * <p/>
     * <em>Important</em>: this may be a palette index.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @return the foreground color at that index.
     */  CompletableFuture<int> getForegroundColorAsync(int column, int row);

    /**
     * Whether the foreground color of the text buffer at the specified
     * location if from the color palette.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @return whether the foreground at that index is from the palette.
     */  CompletableFuture<boolean> isForegroundFromPaletteAsync(int column, int row);

    /**
     * Get the background color of the text buffer at the specified location.
     * <p/>
     * <em>Important</em>: this may be a palette index.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @return the background color at that index.
     */  CompletableFuture<int> getBackgroundColorAsync(int column, int row);

    /**
     * Whether the background color of the text buffer at the specified
     * location if from the color palette.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @return whether the background at that index is from the palette.
     */  CompletableFuture<boolean> isBackgroundFromPaletteAsync(int column, int row);

    /**
     * Overwrites a portion of the text in raw mode.
     * <p/>
     * This will copy the given char array into the buffer, starting at the
     * specified column and row. The array is expected to be indexed row-
     * first, i.e. the first dimension is the vertical axis, the second
     * the horizontal.
     * <p/>
     * <em>Important</em>: this performs no checks as to whether something
     * actually changed. It will always send the changed patch to clients.
     * It will also not crop the specified array to the actually used range.
     * In other words, this is not intended to be exposed as-is to user code,
     * it should always be called with validated, and, as necessary, cropped
     * values.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @param text   the text to write.
     */  CompletableFuture<Void> rawSetTextAsync(int column, int row, char[][] text);

    /**
     * Overwrites a portion of the foreground color information in raw mode.
     * <p/>
     * This will convert the specified  CompletableFuture<RGB> dataAsync(in <tt>0xRRGGBB</tt> format)
     * to the internal, packed representation and copy it into the buffer,
     * starting at the specified column and row. The array is expected to be
     * indexed row-first, i.e. the first dimension is the vertical axis, the
     * second the horizontal.
     * <p/>
     * <em>Important</em>: this performs no checks as to whether something
     * actually changed. It will always send the changed patch to clients.
     * It will also not crop the specified array to the actually used range.
     * In other words, this is not intended to be exposed as-is to user code,
     * it should always be called with validated, and, as necessary, cropped
     * values.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @param color  the foreground color data to write.
     */  CompletableFuture<Void> rawSetForegroundAsync(int column, int row, int[][] color);

    /**
     * Overwrites a portion of the background color information in raw mode.
     * <p/>
     * This will convert the specified  CompletableFuture<RGB> dataAsync(in <tt>0xRRGGBB</tt> format)
     * to the internal, packed representation and copy it into the buffer,
     * starting at the specified column and row. The array is expected to be
     * indexed row-first, i.e. the first dimension is the vertical axis, the
     * second the horizontal.
     * <p/>
     * <em>Important</em>: this performs no checks as to whether something
     * actually changed. It will always send the changed patch to clients.
     * It will also not crop the specified array to the actually used range.
     * In other words, this is not intended to be exposed as-is to user code,
     * it should always be called with validated, and, as necessary, cropped
     * values.
     *
     * @param column the horizontal index.
     * @param row    the vertical index.
     * @param color  the background color data to write.
     */  CompletableFuture<Void> rawSetBackgroundAsync(int column, int row, int[][] color);

    // ----------------------------------------------------------------------- //

    /**
     * Renders the <em>text</em> displayed on the buffer.
     * <p/>
     * You are responsible for setting up the actual context and applying any
     * transformations necessary to properly position and scale the text before
     * calling this. The text should be rendered on a black background.
     * <p/>
     * You can use this to either render the text in a GUI or in the world.
     *
     * @return <tt>true</tt> if the displayed content changed since the last
     * call to this method.
     */
    @OnlyIn(Dist.CLIENT)  CompletableFuture<boolean> renderTextAsync(MatrixStack stack);

    /**
     * The natural width of the rendered text.
     * <p/>
     * This is the width of the complete text buffer, in pixels. In other
     * words, this is the width of the buffer in chars times the actual width
     * of a single char in pixels.
     *
     * @return the total width of the rendered buffer, in pixels.
     */
    @OnlyIn(Dist.CLIENT)  CompletableFuture<int> renderWidthAsync();

    /**
     * The natural height of the rendered text.
     * <p/>
     * This is the height of the complete text buffer, in pixels. In other
     * words, this is the height of the buffer in chars times the actual height
     * of a single char in pixels.
     *
     * @return the total height of the rendered buffer, in pixels.
     */
    @OnlyIn(Dist.CLIENT)  CompletableFuture<int> renderHeightAsync();

    /**
     * Set whether the contents of the buffer should currently be rendered.
     * <p/>
     * Note that this is automatically overwritten when the buffer's power
     * state changes, i.e. when it runs out of power or gets back power.
     *
     * @param enabled whether the text buffer should be rendered.
     */
    @OnlyIn(Dist.CLIENT)  CompletableFuture<Void> setRenderingEnabledAsync(boolean enabled);

    /**
     * Gets whether the contents of the buffer should currently be rendered.
     *
     * @see #setRenderingEnabled(boolean)
     */
    @OnlyIn(Dist.CLIENT)  CompletableFuture<boolean> isRenderingEnabledAsync();

    // ----------------------------------------------------------------------- //

    /**
     * Signals a key down event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will trigger a message that will be picked up by
     * keyboards, which will then cause a signal in attached machines.
     *
     * @param character the character of the pressed key.
     * @param code      the key code of the pressed key.
     * @param player    the player that pressed the key. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> keyDownAsync(char character, int code, PlayerEntity player);

    /**
     * Signals a key up event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will trigger a message that will be picked up by
     * keyboards, which will then cause a signal in attached machines.
     *
     * @param character the character of the released key.
     * @param code      the key code of the released key.
     * @param player    the player that released the key. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> keyUpAsync(char character, int code, PlayerEntity player);

    /**
     * Signals a code-point (text) event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will trigger a message that will be picked up by
     * keyboards, which will then cause a signal in attached machines.
     *
     * @param character the character of the released key.
     * @param codePoint     the code point being typed.
     * @param player        the player that typed the code point. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> textInputAsync(int codePoint, PlayerEntity player);

    /**
     * Signals a clipboard paste event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will trigger a message that will be picked up by
     * keyboards, which will then cause a signal in attached machines.
     *
     * @param value  the text that was pasted.
     * @param player the player that pasted the text. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> clipboardAsync(String value, PlayerEntity player);

    /**
     * Signals a mouse button down event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will cause a signal in attached machines.
     *
     * @param x      the horizontal coordinate of the mouse, in characters.
     * @param y      the vertical coordinate of the mouse, in characters.
     * @param button the button of the mouse that was pressed.
     * @param player the player that pressed the mouse button. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> mouseDownAsync(double x, double y, int button, PlayerEntity player);

    /**
     * Signals a mouse drag event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will cause a signal in attached machines.
     *
     * @param x      the horizontal coordinate of the mouse, in characters.
     * @param y      the vertical coordinate of the mouse, in characters.
     * @param button the button of the mouse that is pressed.
     * @param player the player that moved the mouse. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> mouseDragAsync(double x, double y, int button, PlayerEntity player);

    /**
     * Signals a mouse button release event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will cause a signal in attached machines.
     *
     * @param x      the horizontal coordinate of the mouse, in characters.
     * @param y      the vertical coordinate of the mouse, in characters.
     * @param button the button of the mouse that was released.
     * @param player the player that released the mouse button. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> mouseUpAsync(double x, double y, int button, PlayerEntity player);

    /**
     * Signals a mouse wheel scroll event for the buffer.
     * <p/>
     * On the client side this causes a packet to be sent to the server. On the
     * server side this will cause a signal in attached machines.
     *
     * @param x      the horizontal coordinate of the mouse, in characters.
     * @param y      the vertical coordinate of the mouse, in characters.
     * @param delta  indicates the direction of the mouse scroll.
     * @param player the player that scrolled the mouse wheel. Pass <tt>null</tt> on the client side.
     */  CompletableFuture<Void> mouseScrollAsync(double x, double y, int delta, PlayerEntity player);

    // ----------------------------------------------------------------------- //

    /**
     * Used when setting a buffer's maximum color depth.
     */
    enum ColorDepth {
        /**
         * Monochrome color, black and white.
         */
        OneBit,

        /**
         * 16 color palette, defaults to Minecraft colors.
         */
        FourBit,

        /**
         * 240 colors, 16 color palette, defaults to grayscale.
         */
        EightBit
    }
}
