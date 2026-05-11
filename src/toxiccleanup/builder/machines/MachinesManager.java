package toxiccleanup.builder.machines;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.util.RandomNumberGenerator;
import toxiccleanup.builder.GameState;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

import java.util.ArrayList;

/**
 * The concrete implementation of {@link Machines} for the
 * {@link toxiccleanup.builder.ToxicCleanup} game. {@link MachinesManager} is responsible for:
 *
 * <ul>
 *   <li>Tracking the current power level (starts at 14 by default; capped at 14).</li>
 *   <li>Spending power when a machine is built; each machine type has a fixed {@code COST}
 *       constant on its class (e.g. {@link SolarPanel#COST}, {@link Teleporter#COST},
 *       {@link Pump#COST}), {@link LightningRod#COST}.</li>
 *   <li>Constructing and returning new machine instances when there is sufficient power,
 *       or returning {@code null} if the power cost cannot be met.</li>
 *   <li>Tracking all teleporter positions so that the {@link Teleporter} can retrieve a
 *       destination when the player activates one.</li>
 * </ul>
 */
public class MachinesManager implements Machines {
    private static final int DEFAULT_POWER = 14;
    private static final int maxPower = MachinesManager.DEFAULT_POWER;
    private final ArrayList<Positionable> teleporterPositions = new ArrayList<>();
    private TickTimer teleporterCooldown = new RepeatingTimer(TELEPORTER_COOLDOWN);
    private static final int TELEPORTER_COOLDOWN = 20;
    private final RandomNumberGenerator random = new RandomNumberGenerator();
    private int power;

    /**
     * Constructs a new {@link MachinesManager} starting at full power (14).
     */
    public MachinesManager() {
        power = MachinesManager.DEFAULT_POWER;
    }

    /**
     * Here for testability purposes. Allows us to inject an alternative {@link TickTimer}
     * to override the default internal timer used for the teleportation systems cooldown.
     *
     * @param teleporterCooldownTimer the new {@link TickTimer} we want our {@link MachinesManager}
     *                                to use instead of the default internal {@link TickTimer}.
     */
    public MachinesManager(TickTimer teleporterCooldownTimer) {
        this.teleporterCooldown = teleporterCooldownTimer;
    }

    /**
     * Constructs a new {@link MachinesManager} with the given amount of starting power.
     * Maximum power is fixed at the default (14).
     *
     * @param power the starting power level; clamped to [0, 14] if out of range.
     */
    public MachinesManager(int power) {
        this.power = Math.clamp(power, 0, maxPower);
    }

    @Override
    public boolean hasRequiredPower(int powerRequirement) {
        return power >= powerRequirement;
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public void setPower(int value) {
        power = Math.clamp(value, 0, maxPower);
    }

    @Override
    public int getMaxPower() {
        return maxPower;
    }

    @Override
    public void adjust(int amount) {
        this.gainPower(amount);
    }

    private void gainPower(int amount) {
        power += amount;
        power = Math.clamp(power, 0, maxPower);
    }

    @Override
    public SolarPanel spawnSolarPanel(Positionable position) {
        if (power >= SolarPanel.COST) {
            power -= SolarPanel.COST;
            return new SolarPanel(position);
        }
        return null;
    }

    @Override
    public LightningRod spawnLightningRod(Positionable position) {
        if (power >= LightningRod.COST) {
            power -= LightningRod.COST;
            return new LightningRod(position);
        }
        return null;
    }

    @Override
    public Teleporter spawnTeleporter(Positionable position) {
        if (power >= Teleporter.COST) {
            power -= Teleporter.COST;
            teleporterPositions.add(new Position(position.getX(), position.getY()));
            return new Teleporter(position);
        }
        return null;
    }

    /**
     * Returns the position of a teleporter other than the one at {@code excludedPosition},
     * chosen at random from all registered teleporter locations.
     *
     * @param excludedPosition the position of the teleporter the player is currently standing on.
     * @return the next teleporter position, or {@code excludedPosition} on cooldown.
     */
    @Override
    public Positionable getNextTeleporterPosition(Positionable excludedPosition) {
        if (teleporterPositions.size() == 1) {
            return teleporterPositions.getFirst();
        }
        if (!teleporterCooldown.isFinished()) {
            return excludedPosition;
        }
        final ArrayList<Positionable> validPositions = new ArrayList<>();
        for (Positionable position : teleporterPositions) {
            // TYPO FIX: was (position.getX() != excludedPosition.getX()
            //               && position.getY() != excludedPosition.getY())
            final boolean notOverlappingExcludedPosition = !(position.equals(excludedPosition));
            if (notOverlappingExcludedPosition) {
                validPositions.add(position);
            }
        }
        if (validPositions.isEmpty()) {
            return excludedPosition;
        }
        int randomIndex = random.nextInt(validPositions.size());
        return validPositions.get(randomIndex);
    }

    @Override
    public Pump spawnPump(Positionable position, Adjustable adjustable) {
        if (power >= Pump.COST) {
            power -= Pump.COST;
            return new Pump(position, adjustable);
        }
        return null;
    }

    @Override
    public void tick(EngineState state, GameState game) {
        teleporterCooldown.tick();
    }
}