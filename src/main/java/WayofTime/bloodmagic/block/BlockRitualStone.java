package WayofTime.bloodmagic.block;

import java.util.ArrayList;
import java.util.List;

import WayofTime.bloodmagic.util.helper.TextHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.ritual.EnumRuneType;
import WayofTime.bloodmagic.api.ritual.IRitualStone;
import WayofTime.bloodmagic.block.base.BlockString;
import WayofTime.bloodmagic.client.IVariantProvider;
import WayofTime.bloodmagic.registry.ModBlocks;

public class BlockRitualStone extends BlockString implements IRitualStone, IVariantProvider
{
    public static final String[] names = { "blank", "water", "fire", "earth", "air", "dusk", "dawn" };

    public BlockRitualStone()
    {
        super(Material.IRON, names);

        setUnlocalizedName(Constants.Mod.MODID + ".ritualStone.");
        setCreativeTab(BloodMagic.tabBloodMagic);
        setSoundType(SoundType.STONE);
        setHardness(2.0F);
        setResistance(5.0F);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.decoration.safe"));
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return false;
    }

    @Override
    public boolean isRuneType(World world, BlockPos pos, EnumRuneType runeType)
    {
        return runeType.toString().equals(names[getMetaFromState(world.getBlockState(pos))]);
    }

    @Override
    public void setRuneType(World world, BlockPos pos, EnumRuneType runeType)
    {
        int meta = runeType.ordinal();
        IBlockState newState = ModBlocks.RITUAL_STONE.getStateFromMeta(meta);
        world.setBlockState(pos, newState);
    }

    @Override
    public List<Pair<Integer, String>> getVariants()
    {
        List<Pair<Integer, String>> ret = new ArrayList<Pair<Integer, String>>();
        for (int i = 0; i < names.length; i++)
            ret.add(new ImmutablePair<Integer, String>(i, "type=" + names[i]));
        return ret;
    }
}
