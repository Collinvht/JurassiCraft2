package net.timeless.jurassicraft.common.entity;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.timeless.jurassicraft.common.entity.base.EntityDinosaurAggressive;
import net.timeless.unilib.common.animation.ChainBuffer;

public class EntityAchillobator extends EntityDinosaurAggressive  //implements ICarnivore, IEntityAICreature
{

	private static final Class[] targets = {EntityCompsognathus.class, EntityPlayer.class, EntityDilophosaurus.class, EntityDimorphodon.class, EntityDodo.class, EntityLeaellynasaura.class, EntityHypsilophodon.class, EntitySegisaurus.class, EntityProtoceratops.class, EntityOthnielia.class, EntityMicroceratus.class};
	private static final Class[] deftargets = {EntityPlayer.class, EntityTyrannosaurus.class, EntityGiganotosaurus.class, EntitySpinosaurus.class}; 
	public ChainBuffer tailBuffer = new ChainBuffer(6);

    public EntityAchillobator(World world)
    {
        super(world);
        this.attackCreature(EntityPlayer.class, 1);
        
        for (int i = 0; i < targets.length; i++)
        {
            this.attackCreature(targets[i], new Random().nextInt(3)+1);
        }
        
        for (int j = 0; j < targets.length; j++)
        {
            this.defendFromAttacker(deftargets[j], new Random().nextInt(3)+1);
        }

    }

    public void onUpdate()
    {
        this.tailBuffer.calculateChainSwingBuffer(68.0F, 5, 4.0F, this);
        super.onUpdate();
    }
}
