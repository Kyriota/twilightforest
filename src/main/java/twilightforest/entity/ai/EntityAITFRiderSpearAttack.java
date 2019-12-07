package twilightforest.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import twilightforest.entity.EntityTFGoblinKnightLower;
import twilightforest.entity.EntityTFGoblinKnightUpper;

public class EntityAITFRiderSpearAttack extends Goal {

	private EntityTFGoblinKnightLower entity;

	public EntityAITFRiderSpearAttack(EntityTFGoblinKnightLower lowerKnight) {
		this.entity = lowerKnight;
		this.setMutexBits(3); // Prevent moving
	}

	@Override
	public boolean shouldExecute() {
		if (!this.entity.getPassengers().isEmpty() && this.entity.getPassengers().get(0) instanceof EntityTFGoblinKnightUpper) {
			int timer = ((EntityTFGoblinKnightUpper) this.entity.getPassengers().get(0)).heavySpearTimer;
			return timer > 0 && timer < EntityTFGoblinKnightUpper.HEAVY_SPEAR_TIMER_START;
		} else {
			return false;
		}
	}

}
