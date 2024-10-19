package rustic.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rustic.common.potions.EventHandlerPotions;

public class MessageFirePowerAttack implements IMessage {

	public MessageFirePowerAttack() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub

	}
	
	
	public static class MessageHolder implements IMessageHandler<MessageFirePowerAttack, IMessage> {
		
		@Override
		public IMessage onMessage(final MessageFirePowerAttack message, final MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				EventHandlerPotions.doFirePowerAttack(player);
			});
			return null;
		}
		
	}

}
