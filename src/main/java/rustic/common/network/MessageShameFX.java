package rustic.common.network;

import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.items.ModItems;
import rustic.common.tileentity.ITileEntitySyncable;

public class MessageShameFX implements IMessage {

	public static Random rand = new Random();
	int num = 0;
	double x = 0;
	double y = 0;
	double z = 0;
	double xVel = 0;
	double yVel = 0;
	double zVel = 0;
	double width = 0;
	double height = 0;

	public MessageShameFX() {

	}

	public MessageShameFX(int num, double x, double y, double z, double xVel, double yVel, double zVel, double width, double height) {
		this.num = num;
		this.x = x;
		this.y = y;
		this.z = z;
		this.xVel = xVel;
		this.yVel = yVel;
		this.zVel = zVel;
		this.width = width;
		this.height = height;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.num = buf.readInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.xVel = buf.readDouble();
		this.yVel = buf.readDouble();
		this.zVel = buf.readDouble();
		this.width = buf.readDouble();
		this.height = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(num);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(xVel);
		buf.writeDouble(yVel);
		buf.writeDouble(zVel);
		buf.writeDouble(width);
		buf.writeDouble(height);
	}

	public static class MessageHolder implements IMessageHandler<MessageShameFX, IMessage> {
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(final MessageShameFX message, final MessageContext ctx) {
			// Minecraft.getMinecraft().addScheduledTask(() -> {
			World world = Minecraft.getMinecraft().world;
			if (world.isRemote) {
				for (int i = 0; i < message.num; i++) {
					double x = message.x + message.xVel;
					double y = message.y + (message.height * message.rand.nextDouble()) + message.yVel;
					double z = message.z + message.zVel;
					switch (message.rand.nextInt(4)) {
					case 3:
						x += message.width * (message.rand.nextDouble() - 0.5);
						z -= message.width * 0.5;
						break;
					case 2:
						x += message.width * (message.rand.nextDouble() - 0.5);
						z += message.width * 0.5;
						break;
					case 1:
						z += message.width * (message.rand.nextDouble() - 0.5);
						x -= message.width * 0.5;
						break;
					default:
						z += message.width * (message.rand.nextDouble() - 0.5);
						x += message.width * 0.5;
						break;
					}
					world.spawnParticle(EnumParticleTypes.ITEM_CRACK, true, x, y, z, message.xVel, message.yVel, message.zVel, Item.getIdFromItem(ModItems.TOMATO));
				}
			}
			// });
			return null;
		}
	}

}
