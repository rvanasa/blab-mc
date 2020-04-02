package io.halfbeard.blab;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.stream.Collectors;

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
            BookData data = BlabUtils.randomBookData();

            // Create written book
            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK, 1);
            stack.setTagInfo("title", StringNBT.valueOf(data.title));
            stack.setTagInfo("author", StringNBT.valueOf(data.author));
            stack.setTagInfo("resolved", IntNBT.valueOf(1));

            ListNBT pages = new ListNBT();
            for(String text : data.pages) {
                JsonObject page = new JsonObject();
                page.add("text", new JsonPrimitive(text));
                pages.add(StringNBT.valueOf(new GsonBuilder().create().toJson(page)));
            }
            stack.setTagInfo("pages", pages);

//            EditBookScreen screen = new EditBookScreen(player, stack, player.getActiveHand());
//            for(int i = 0; i < text.length(); i++) {
//                char c = text.charAt(i);
//                screen.charTyped(c, 0);
//            }
//            screen.

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
