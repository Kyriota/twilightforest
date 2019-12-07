package twilightforest.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTFSlimeProjectile extends EntityTFThrowable {

	public EntityTFSlimeProjectile(World world) {
		super(world);
	}

	public EntityTFSlimeProjectile(World world, LivingEntity thrower) {
		super(world, thrower);
	}

	@Override
	public void tick() {
		super.tick();
		makeTrail();
	}

	@Override
	protected float getGravityVelocity() {
		return 0.006F;
	}

	private void makeTrail() {
		for (int i = 0; i < 2; i++) {
			double dx = posX + 0.5 * (rand.nextDouble() - rand.nextDouble());
			double dy = posY + 0.5 * (rand.nextDouble() - rand.nextDouble());
			double dz = posZ + 0.5 * (rand.nextDouble() - rand.nextDouble());
			world.addParticle(ParticleTypes.ITEM_SLIME, dx, dy, dz, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		super.attackEntityFrom(source, amount);
		die();
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.world.addParticle(ParticleTypes.ITEM_SLIME, this.posX, this.posY, this.posZ, rand.nextGaussian() * 0.05D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.05D);
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	protected void onImpact(RayTraceResult target) {
		// only damage living things
		if (!world.isRemote && target.entityHit instanceof LivingEntity) {
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 8);
			// TODO: damage armor?
		}

		die();
	}

	private void die() {
		if (!this.world.isRemote) {
			this.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
			this.setDead();
			this.world.setEntityState(this, (byte) 3);
		}
	}

}
