package net.reuxertz.ecoapi.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.reuxertz.ecocraft.common.block.properties.PropertyFloat;

import java.util.Random;

public class BaseBlockFluid extends BlockFluidClassic
{
    public static final PropertyFloat HEIGHT_NW = new PropertyFloat("height_nw", 0F, 1F), HEIGHT_SW = new PropertyFloat("height_sw", 0F, 1F),
            HEIGHT_SE = new PropertyFloat("height_se", 0F, 1F), HEIGHT_NE = new PropertyFloat("height_ne", 0F, 1F);
    public static final PropertyFloat FLOW_DIRECTION = new PropertyFloat("flow_direction");

    public BaseBlockFluid(Fluid fluid, Material material)
    {
        super(fluid, material);
        setCreativeTab(CreativeTabs.tabMisc);
        this.tickRate = 35;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        //if (rand.nextDouble() > .5)
        //   return;

        int quantaRemaining = quantaPerBlock - ((Integer)state.getValue(LEVEL)).intValue();
        int expQuanta = -101;

        // check adjacent block levels if non-source
        if (quantaRemaining < quantaPerBlock)
        {
            if (world.getBlockState(pos.add( 0, -densityDir,  0)).getBlock() == this ||
                    world.getBlockState(pos.add(-1, -densityDir,  0)).getBlock() == this ||
                    world.getBlockState(pos.add( 1, -densityDir,  0)).getBlock() == this ||
                    world.getBlockState(pos.add( 0, -densityDir, -1)).getBlock() == this ||
                    world.getBlockState(pos.add( 0, -densityDir,  1)).getBlock() == this)
            {
                expQuanta = quantaPerBlock - 1;
            }
            else
            {
                int maxQuanta = -100;
                maxQuanta = getLargerQuanta(world, pos.add(-1, 0,  0), maxQuanta);
                maxQuanta = getLargerQuanta(world, pos.add( 1, 0,  0), maxQuanta);
                maxQuanta = getLargerQuanta(world, pos.add( 0, 0, -1), maxQuanta);
                maxQuanta = getLargerQuanta(world, pos.add( 0, 0,  1), maxQuanta);

                expQuanta = maxQuanta - 1;
            }

            // decay calculation
            if (expQuanta != quantaRemaining)
            {
                quantaRemaining = expQuanta;

                if (expQuanta <= 0)
                {
                    world.setBlockToAir(pos);
                }
                else
                {
                    world.setBlockState(pos, state.withProperty(LEVEL, quantaPerBlock - expQuanta), 2);
                    world.scheduleUpdate(pos, this, tickRate);
                    world.notifyNeighborsOfStateChange(pos, this);
                }
            }
        }
        // This is a "source" block, set meta to zero, and send a server only update
        else if (quantaRemaining >= quantaPerBlock)
        {
            world.setBlockState(pos, this.getDefaultState(), 2);
        }

        // Flow vertically if possible
        if (canDisplace(world, pos.up(densityDir)))
        {
            flowIntoBlock(world, pos.up(densityDir), 1);
            return;
        }

        // Flow outward if possible
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
        if (flowMeta >= quantaPerBlock)
        {
            return;
        }

        boolean b1 = isSourceBlock(world, pos);
        boolean b2 = !isFlowingVertically(world, pos);
        if (b1 || b2)
        {
            if (world.getBlockState(pos.down(densityDir)).getBlock() == this)
            {
                flowMeta = 1;
            }
            boolean flowTo[] = getOptimalFlowDirections(world, pos);

            if (flowTo[0]) flowIntoBlock(world, pos.add(-1, 0,  0), flowMeta);
            if (flowTo[1]) flowIntoBlock(world, pos.add( 1, 0,  0), flowMeta);
            if (flowTo[2]) flowIntoBlock(world, pos.add( 0, 0, -1), flowMeta);
            if (flowTo[3]) flowIntoBlock(world, pos.add( 0, 0,  1), flowMeta);
        }
    }

    @Override
    public boolean canDisplace(IBlockAccess world, BlockPos pos) {

        Block b = world.getBlockState(pos).getBlock();
        if (b.getMaterial().isLiquid())
            return false;
        return super.canDisplace(world, pos);
    }

    @Override
    public boolean displaceIfPossible(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock().getMaterial().isLiquid())
            return false;
        return super.displaceIfPossible(world, pos);
    }

    @Override
    protected BlockState createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[] {LEVEL}, new IUnlistedProperty[] {HEIGHT_SW, HEIGHT_NW, HEIGHT_SE, HEIGHT_NE, FLOW_DIRECTION});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        state = super.getExtendedState(state, world, pos);

        float heightNW, heightSW, heightSE, heightNE;
        float flow11 = getFluidHeightForRender(world, pos);

        if (flow11 != 1)
        {
            float flow00 = getFluidHeightForRender(world, pos.add(-1, 0, -1));
            float flow01 = getFluidHeightForRender(world, pos.add(-1, 0,  0));
            float flow02 = getFluidHeightForRender(world, pos.add(-1, 0,  1));
            float flow10 = getFluidHeightForRender(world, pos.add( 0, 0, -1));
            float flow12 = getFluidHeightForRender(world, pos.add( 0, 0,  1));
            float flow20 = getFluidHeightForRender(world, pos.add( 1, 0, -1));
            float flow21 = getFluidHeightForRender(world, pos.add( 1, 0,  0));
            float flow22 = getFluidHeightForRender(world, pos.add( 1, 0,  1));

            heightNW = getFluidHeightAverage(new float[]{ flow00, flow01, flow10, flow11 });
            heightSW = getFluidHeightAverage(new float[]{ flow01, flow02, flow12, flow11 });
            heightSE = getFluidHeightAverage(new float[]{ flow12, flow21, flow22, flow11 });
            heightNE = getFluidHeightAverage(new float[]{ flow10, flow20, flow21, flow11 });
        }
        else
        {
            heightNW = flow11;
            heightSW = flow11;
            heightSE = flow11;
            heightNE = flow11;
        }

        IExtendedBlockState extState = (IExtendedBlockState) state;
        extState = extState.withProperty(HEIGHT_NW, heightNW).withProperty(HEIGHT_SW, heightSW);
        extState = extState.withProperty(HEIGHT_SE, heightSE).withProperty(HEIGHT_NE, heightNE);
        extState = extState.withProperty(FLOW_DIRECTION, (float) BlockFluidBase.getFlowDirection(world, pos));

        return extState;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if(side == EnumFacing.UP)
            return world.getBlockState(pos).getBlock() != this;
        else
            return super.shouldSideBeRendered(world, pos, side);
    }

    public float getFluidHeightForRender(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        Block verticalOrigin = world.getBlockState(pos.down(this.densityDir)).getBlock();
        if (state.getBlock() == this)
        {
            if (verticalOrigin.getMaterial().isLiquid() || verticalOrigin instanceof IFluidBlock)
            {
                return 1;
            }

            if ((Integer) state.getValue(LEVEL) == this.getMaxRenderHeightMeta())
            {
                return (8F / 9F);
            }
        }
        return !state.getBlock().getMaterial().isSolid() && verticalOrigin == this ? 1 : this.getQuantaPercentage(world, pos) * (8F / 9F);
    }

    public float getFluidHeightAverage(float[] height)
    {
        float total = 0;
        int count = 0;

        float end = 0;

        for (int i = 0; i < height.length; i++)
        {
            if (height[i] >= 0.875F && end != 1F)
            {
                end = height[i];
            }

            if (height[i] >= 0)
            {
                total += height[i];
                count++;
            }
        }

        if (end == 0)
            end = total / count;

        return end;
    }

    @Override
    public int getRenderType()
    {
        return 3;
    }
}