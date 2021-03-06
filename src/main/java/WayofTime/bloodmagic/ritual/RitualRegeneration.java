package WayofTime.bloodmagic.ritual;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.ritual.AreaDescriptor;
import WayofTime.bloodmagic.api.ritual.EnumRuneType;
import WayofTime.bloodmagic.api.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.ritual.RitualComponent;
import WayofTime.bloodmagic.api.saving.SoulNetwork;
import WayofTime.bloodmagic.api.util.helper.NetworkHelper;

public class RitualRegeneration extends Ritual
{
    public static final String HEAL_RANGE = "heal";

    public static final int SACRIFICE_AMOUNT = 100;

    public RitualRegeneration()
    {
        super("ritualRegeneration", 0, 25000, "ritual." + Constants.Mod.MODID + ".regenerationRitual");
        addBlockRange(HEAL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-15, -15, -15), 31));
        setMaximumVolumeAndDistanceOfRange(HEAL_RANGE, 0, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone)
    {
        World world = masterRitualStone.getWorldObj();
        SoulNetwork network = NetworkHelper.getSoulNetwork(masterRitualStone.getOwner());
        int currentEssence = network.getCurrentEssence();

        if (currentEssence < getRefreshCost())
        {
            network.causeNausea();
            return;
        }

        BlockPos pos = masterRitualStone.getBlockPos();

        int maxEffects = currentEssence / getRefreshCost();
        int totalEffects = 0;

        int totalCost = 0;

        AreaDescriptor damageRange = getBlockRange(HEAL_RANGE);
        AxisAlignedBB range = damageRange.getAABB(pos);

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, range);

        for (EntityLivingBase entity : entities)
        {
            float health = entity.getHealth();
            if (health <= entity.getMaxHealth() - 1)
            {
                if (entity.isPotionApplicable(new PotionEffect(MobEffects.REGENERATION)))
                {
                    if (entity instanceof EntityPlayer)
                    {
                        totalCost += getRefreshCost();
                    } else
                    {
                        totalCost += getRefreshCost() / 10;
                    }

                    entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 0, false, false));

                    totalEffects++;

                    if (totalEffects >= maxEffects)
                    {
                        break;
                    }
                }
            }
        }

        network.syphon(totalCost);
    }

    @Override
    public int getRefreshTime()
    {
        return 50;
    }

    @Override
    public int getRefreshCost()
    {
        return 200;
    }

    @Override
    public ArrayList<RitualComponent> getComponents()
    {
        ArrayList<RitualComponent> components = new ArrayList<RitualComponent>();

        components.add(new RitualComponent(new BlockPos(4, 0, 0), EnumRuneType.AIR));
        components.add(new RitualComponent(new BlockPos(5, 0, -1), EnumRuneType.AIR));
        components.add(new RitualComponent(new BlockPos(5, 0, 1), EnumRuneType.AIR));
        components.add(new RitualComponent(new BlockPos(-4, 0, 0), EnumRuneType.AIR));
        components.add(new RitualComponent(new BlockPos(-5, 0, -1), EnumRuneType.AIR));
        components.add(new RitualComponent(new BlockPos(-5, 0, 1), EnumRuneType.AIR));
        components.add(new RitualComponent(new BlockPos(0, 0, 4), EnumRuneType.FIRE));
        components.add(new RitualComponent(new BlockPos(1, 0, 5), EnumRuneType.FIRE));
        components.add(new RitualComponent(new BlockPos(-1, 0, 5), EnumRuneType.FIRE));
        components.add(new RitualComponent(new BlockPos(0, 0, -4), EnumRuneType.FIRE));
        components.add(new RitualComponent(new BlockPos(1, 0, -5), EnumRuneType.FIRE));
        components.add(new RitualComponent(new BlockPos(-1, 0, -5), EnumRuneType.FIRE));
        this.addOffsetRunes(components, 3, 5, 0, EnumRuneType.WATER);
        this.addCornerRunes(components, 3, 0, EnumRuneType.DUSK);
        this.addOffsetRunes(components, 4, 5, 0, EnumRuneType.EARTH);
        this.addOffsetRunes(components, 4, 5, -1, EnumRuneType.EARTH);
        this.addCornerRunes(components, 5, 0, EnumRuneType.EARTH);

        return components;
    }

    @Override
    public Ritual getNewCopy()
    {
        return new RitualRegeneration();
    }
}
