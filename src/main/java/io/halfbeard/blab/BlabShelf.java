package io.halfbeard.blab;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlabShelf extends Block {

    protected BlabShelf() {
        super(Block.Properties.create(Material.WOOD)
                .hardnessAndResistance(1.5F)
                .sound(SoundType.WOOD));
    }

//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
//    }

    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if(!world.isRemote) {
            // Create written book
            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK, 1);
            stack.setTagInfo("title", StringNBT.valueOf("A custom book"));
            stack.setTagInfo("author", StringNBT.valueOf("Somebody"));

            ListNBT pages = new ListNBT();
            // TODO: page splitting logic
            pages.add(StringNBT.valueOf("{\"text\":\"Steve is the Zodiac Killer\"}"));

            stack.setTagInfo("pages", pages);

            // Give book to player
            if(!player.addItemStackToInventory(stack)) {
                ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                world.addEntity(entity);
            }

            // Convert to regular bookshelf
            world.setBlockState(pos, Blocks.BOOKSHELF.getDefaultState());
        }
        return ActionResultType.SUCCESS;
    }
}
