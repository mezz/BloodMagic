package WayofTime.bloodmagic.item.sigil;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import WayofTime.bloodmagic.api.altar.IBloodAltar;
import WayofTime.bloodmagic.api.iface.IAltarReader;
import WayofTime.bloodmagic.api.soul.EnumDemonWillType;
import WayofTime.bloodmagic.api.util.helper.NetworkHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.entity.mob.EntityCorruptedSheep;
import WayofTime.bloodmagic.tile.TileIncenseAltar;
import WayofTime.bloodmagic.tile.TileInversionPillar;
import WayofTime.bloodmagic.util.ChatUtil;
import WayofTime.bloodmagic.util.helper.NumeralHelper;

public class ItemSigilDivination extends ItemSigilBase implements IAltarReader
{
    public ItemSigilDivination()
    {
        super("divination");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
//        if (world instanceof WorldServer)
//        {
//            System.out.println("Testing...");
////            BuildTestStructure s = new BuildTestStructure();
////            s.placeStructureAtPosition(new Random(), Rotation.CLOCKWISE_180, (WorldServer) world, player.getPosition(), 0);
//            DungeonTester.testDungeonElementWithOutput((WorldServer) world, player.getPosition());
//        }

//        if (!world.isRemote)
//        {
//            EntityCorruptedSheep fred = new EntityCorruptedSheep(world, EnumDemonWillType.DESTRUCTIVE);
//            fred.setPosition(player.posX, player.posY, player.posZ);
//            world.spawnEntityInWorld(fred);
//        }

        if (!world.isRemote)
        {
            super.onItemRightClick(stack, world, player, hand);

            RayTraceResult position = rayTrace(world, player, false);

            if (position == null)
            {
                int currentEssence = NetworkHelper.getSoulNetwork(getOwnerUUID(stack)).getCurrentEssence();
                List<ITextComponent> toSend = new ArrayList<ITextComponent>();
                if (!getOwnerName(stack).equals(PlayerHelper.getUsernameFromPlayer(player)))
                    toSend.add(new TextComponentTranslation(tooltipBase + "otherNetwork", getOwnerName(stack)));
                toSend.add(new TextComponentTranslation(tooltipBase + "currentEssence", currentEssence));
                ChatUtil.sendNoSpam(player, toSend.toArray(new ITextComponent[toSend.size()]));
            } else
            {
                if (position.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    TileEntity tile = world.getTileEntity(position.getBlockPos());

                    if (tile != null && tile instanceof IBloodAltar)
                    {
                        IBloodAltar altar = (IBloodAltar) tile;
                        int tier = altar.getTier().ordinal() + 1;
                        int currentEssence = altar.getCurrentBlood();
                        int capacity = altar.getCapacity();
                        altar.checkTier();
                        ChatUtil.sendNoSpam(player, new TextComponentTranslation(tooltipBase + "currentAltarTier", NumeralHelper.toRoman(tier)), new TextComponentTranslation(tooltipBase + "currentEssence", currentEssence), new TextComponentTranslation(tooltipBase + "currentAltarCapacity", capacity));
                    } else if (tile != null && tile instanceof TileIncenseAltar)
                    {
                        TileIncenseAltar altar = (TileIncenseAltar) tile;
                        altar.recheckConstruction();
                        double tranquility = altar.tranquility;
                        ChatUtil.sendNoSpam(player, new TextComponentTranslation(tooltipBase + "currentTranquility", (int) ((100D * (int) (100 * tranquility)) / 100d)), new TextComponentTranslation(tooltipBase + "currentBonus", (int) (100 * altar.incenseAddition)));
                    } else if (tile != null && tile instanceof TileInversionPillar)
                    {
                        TileInversionPillar pillar = (TileInversionPillar) tile;
                        double inversion = pillar.getCurrentInversion();
                        ChatUtil.sendNoSpam(player, new TextComponentTranslation(tooltipBase + "currentInversion", ((int) (10 * inversion)) / 10d));
                    } else

                    {
                        int currentEssence = NetworkHelper.getSoulNetwork(getOwnerUUID(stack)).getCurrentEssence();
                        ChatUtil.sendNoSpam(player, new TextComponentTranslation(tooltipBase + "currentEssence", currentEssence));
                    }
                }
            }
        }

        return super.onItemRightClick(stack, world, player, hand);
    }
}
