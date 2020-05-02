package rustic.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rustic.common.blocks.ModBlocks;

public class MessageDismountChair implements IMessage {

	public MessageDismountChair() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
	
	public static class MessageHolder implements IMessageHandler<MessageDismountChair, IMessage> {
		
		@Override
		public IMessage onMessage(final MessageDismountChair message, final MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				player.dismountRidingEntity();
			});
			return null;
		}
		
	}

}
