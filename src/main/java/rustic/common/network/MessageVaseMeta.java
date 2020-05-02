package rustic.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rustic.common.blocks.ModBlocks;

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
			player.getServerWorld().addScheduledTask(() -> {
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
			});
			return null;
		}
		
	}

}
