package twilightforest.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTFTwilightWandBolt extends EntityTFThrowable {

	@SuppressWarnings("unused")
	public EntityTFTwilightWandBolt(World world) {
		super(world);
	}

	@SuppressWarnings("unused")
	public EntityTFTwilightWandBolt(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityTFTwilightWandBolt(World world, LivingEntity thrower) {
		super(world, thrower);
		shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0, 1.5F, 1.0F);
	}

	@Override
	public void tick() {
		super.tick();
		makeTrail();
	}

	private void makeTrail() {
		for (int i = 0; i < 5; i++) {
			double dx = posX + 0.5 * (rand.nextDouble() - rand.nextDouble());
			double dy = posY + 0.5 * (rand.nextDouble() - rand.nextDouble());
			double dz = posZ + 0.5 * (rand.nextDouble() - rand.nextDouble());

			double s1 = ((rand.nextFloat() * 0.5F) + 0.5F) * 0.17F;  // color
			double s2 = ((rand.nextFloat() * 0.5F) + 0.5F) * 0.80F;  // color
			double s3 = ((rand.nextFloat() * 0.5F) + 0.5F) * 0.69F;  // color

			world.addParticle(ParticleTypes.SPELL_MOB, dx, dy, dz, s1, s2, s3);
		}
	}

	@Override
	protected float getGravityVelocity() {
		return 0.003F;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 3) {
			int itemId = Item.getIdFromItem(Items.ENDER_PEARL);
			for (int i = 0; i < 8; ++i) {
				this.world.addParticle(ParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, rand.nextGaussian() * 0.05D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.05D, itemId);
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
			if (result.entityHit instanceof LivingEntity) {
				result.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), 6);
			}

			this.world.setEntityState(this, (byte) 3);
			this.setDead();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		super.attackEntityFrom(source, amount);

		if (!this.world.isRemote && source.getTrueSource() != null) {
			Vec3d vec3d = source.getTrueSource().getLookVec();
			// reflect faster and more accurately
			this.shoot(vec3d.x, vec3d.y, vec3d.z, 1.5F, 0.1F);  // reflect faster and more accurately

			if (source.getImmediateSource() instanceof LivingEntity) {
				this.owner = (LivingEntity) source.getImmediateSource();
			}
			return true;
		}

		return false;
	}
}
