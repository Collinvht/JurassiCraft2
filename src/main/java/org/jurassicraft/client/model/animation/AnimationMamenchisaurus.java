package org.jurassicraft.client.model.animation;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.timeless.animationapi.client.Animator;
import net.timeless.unilib.client.model.json.IModelAnimator;
import net.timeless.unilib.client.model.json.ModelJson;
import org.jurassicraft.client.model.ModelDinosaur;
import org.jurassicraft.common.entity.EntityMamenchisaurus;

@SideOnly(Side.CLIENT)
public class AnimationMamenchisaurus implements IModelAnimator
{
    @Override
    public void setRotationAngles(ModelJson modelJson, float f, float f1, float rotation, float rotationYaw, float rotationPitch, float partialTicks, Entity e)
    {
        ModelDinosaur model = (ModelDinosaur) modelJson;
		EntityMamenchisaurus entity = (EntityMamenchisaurus) e;
        Animator animator = model.animator;
    }
}