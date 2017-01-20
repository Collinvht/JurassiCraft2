package org.jurassicraft.server.entity;

public final class LegSolverBiped extends LegSolver {
    public final Leg left, right;

    public LegSolverBiped(float forward, float side) {
        super(new Leg(forward, side), new Leg(forward, -side));
        this.left = legs[0];
        this.right = legs[1];
    }
}
