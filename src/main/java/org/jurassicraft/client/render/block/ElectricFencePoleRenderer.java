package org.jurassicraft.client.render.block;

import net.ilexiconn.llibrary.client.model.tabula.TabulaModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.entity.ElectricFencePoleBlockEntity;
import org.jurassicraft.server.block.fence.ElectricFencePoleBlock;
import org.jurassicraft.server.tabula.TabulaModelHelper;
import org.lwjgl.opengl.GL11;

public class ElectricFencePoleRenderer extends TileEntitySpecialRenderer<ElectricFencePoleBlockEntity> {
    private Minecraft mc = Minecraft.getMinecraft();

    private TabulaModel model;
    private ResourceLocation textureInactive;
    private ResourceLocation textureActive;

    public ElectricFencePoleRenderer() {
        try {
            this.model = new TabulaModel(TabulaModelHelper.loadTabulaModel("/assets/jurassicraft/models/block/low_security_fence_pole.tbl"));
            this.textureInactive = new ResourceLocation(JurassiCraft.MODID, "textures/blocks/low_security_fence_pole_inactive.png");
            this.textureActive = new ResourceLocation(JurassiCraft.MODID, "textures/blocks/low_security_fence_pole_active.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renderTileEntityAt(ElectricFencePoleBlockEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);

        double scale = 1.0;
        GlStateManager.scale(scale, -scale, scale);

        boolean active = tile == null;

        boolean north = true;
        boolean south = true;
        boolean west = true;
        boolean east = true;

        if (tile != null) {
            BlockPos position = tile.getPos();
            IBlockState state = tile.getWorld().getBlockState(position).getActualState(tile.getWorld(), position);
            if (state.getBlock() == BlockHandler.LOW_SECURITY_FENCE_POLE) {
                north = state.getValue(ElectricFencePoleBlock.NORTH);
                south = state.getValue(ElectricFencePoleBlock.SOUTH);
                west = state.getValue(ElectricFencePoleBlock.WEST);
                east = state.getValue(ElectricFencePoleBlock.EAST);
            }
        }

        this.model.getCube("Wire base 4").showModel = north;
        this.model.getCube("Wire base 3").showModel = north;

        this.model.getCube("Wire base 2").showModel = east;
        this.model.getCube("Wire base 1").showModel = east;

        this.model.getCube("Wire base 6").showModel = west;
        this.model.getCube("Wire base 5").showModel = west;

        this.model.getCube("Wire base 8").showModel = south;
        this.model.getCube("Wire base 7").showModel = south;

        this.mc.getTextureManager().bindTexture(active ? this.textureActive : this.textureInactive);

        this.model.render(null, 0, 0, 0, 0, 0, 0.0625F);

        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
