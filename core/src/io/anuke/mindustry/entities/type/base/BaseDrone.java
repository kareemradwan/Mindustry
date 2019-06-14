package io.anuke.mindustry.entities.type.base;

import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.mindustry.entities.type.FlyingUnit;
import io.anuke.mindustry.entities.units.UnitState;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.meta.BlockFlag;

import static io.anuke.mindustry.Vars.world;

public abstract class BaseDrone extends FlyingUnit{
    public final UnitState retreat = new UnitState(){
        public void entered(){
            target = null;
        }

        public void update(){
            if(health >= maxHealth()){
                state.set(attack);
            }else if(!targetHasFlag(BlockFlag.repair)){
                retarget(() -> {
                    Tile repairPoint = Geometry.findClosest(x, y, world.indexer.getAllied(team, BlockFlag.repair));
                    if(repairPoint != null){
                        target = repairPoint;
                    }else{
                        setState(getStartState());
                    }
                });
            }else{
                circle(40f);
            }
        }
    };

    @Override
    protected void updateRotation(){
        if(target != null && shouldRotate() && target.dst(this) < type.range){
            rotation = Mathf.slerpDelta(rotation, angleTo(target), 0.3f);
        }else{
            rotation = Mathf.slerpDelta(rotation, velocity.angle(), 0.3f);
        }
    }

    @Override
    public void behavior(){
        if(health <= health * type.retreatPercent){
            setState(retreat);
        }
    }

    public boolean shouldRotate(){
        return state.is(getStartState());
    }

    @Override
    public abstract UnitState getStartState();

}