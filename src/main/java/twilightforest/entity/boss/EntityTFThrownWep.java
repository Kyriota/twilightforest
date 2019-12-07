package twilightforest.entity.boss;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twilightforest.entity.EntityTFThrowable;

public class EntityTFThrownWep extends EntityTFThrowable {

	private static final DataParameter<ItemStack> DATA_ITEMSTACK = EntityDataManager.createKey(EntityTFThrownWep.class, DataSerializers.ITEMSTACK);
	private static final DataParameter<Float> DATA_VELOCITY = EntityDataManager.createKey(EntityTFThrownWep.class, DataSerializers.FLOAT);

	private float projectileDamage = 6;

	public EntityTFThrownWep(World world, LivingEntity thrower) {
		super(world, thrower);
		this.setSize(0.5F, 0.5F);
	}

	public EntityTFThrownWep(World world) {
		super(world);
		this.setSize(0.5F, 0.5F);
	}

	public EntityTFThrownWep setDamage(float damage) {
		projectileDamage = damage;
		return this;
	}

	@Override
	protected void registerData() {
		dataManager.register(DATA_ITEMSTACK, ItemStack.EMPTY);
		dataManager.register(DATA_VELOCITY, 0.001F);
	}

	public EntityTFThrownWep setItem(ItemStack stack) {
		dataManager.set(DATA_ITEMSTACK, stack);
		return this;
	}

	public ItemStack getItem() {
		return dataManager.get(DATA_ITEMSTACK);
	}

	public EntityTFThrownWep setVelocity(float velocity) {
		dataManager.set(DATA_VELOCITY, velocity);
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit instanceof EntityTFKnightPhantom || result.entityHit == this.getThrower()) {
			return;
		}

		if (!world.isRemote) {
			if (result.entityHit != null) {
				result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), projectileDamage);
			}
			world.setEntityState(this, (byte) 3);
			setDead();
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	@Override
	protected float getGravityVelocity() {
		return dataManager.get(DATA_VELOCITY);
	}
}
