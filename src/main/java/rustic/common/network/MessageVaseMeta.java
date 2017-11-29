package rustic.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;

public class MessageVaseMeta implements IMessage {

	int meta = 0;
	
	public MessageVaseMeta() {
		
	}
	
	public MessageVaseMeta(int i) {
		this.meta = i;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.meta = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.meta);
	}
	
	public static class MessageHolder implements IMessageHandler<MessageVaseMeta, IMessage> {
		
		@Override
		public IMessage onMessage(final MessageVaseMeta message, final MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			for (ItemStack stack : player.inventory.mainInventory) {
				if (stack.getItem() == Item.getItemFromBlock(ModBlocks.VASE)) {
					stack.setItemDamage(message.meta);
				}
			}
			for (ItemStack stack : player.inventory.offHandInventory) {
				if (stack.getItem() == Item.getItemFromBlock(ModBlocks.VASE)) {
					stack.setItemDamage(message.meta);
				}
			}
			
			return null;
		}
		
	}

}
