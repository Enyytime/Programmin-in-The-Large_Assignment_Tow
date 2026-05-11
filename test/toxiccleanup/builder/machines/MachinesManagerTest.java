package toxiccleanup.builder.machines;

import org.junit.Before;
import org.junit.Test;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MachinesManager}.
 * Tests power management, machine spawning, and teleporter routing.
 * No mocks used — only the public API of MachinesManager.
 */
public class MachinesManagerTest {

  private static final Position POSITION_A = new Position(100, 100);
  private static final Position POSITION_B = new Position(200, 200);

  private MachinesManager manager;

  @Before
  public void setUp() {
    manager = new MachinesManager();
  }

  // -- Default construction -- //

  @Test
  public void defaultConstructorStartsAtFullPower() {
    assertEquals(14, manager.getPower());
  }

  @Test
  public void defaultConstructorMaxPowerIsFourteen() {
    assertEquals(14, manager.getMaxPower());
  }

  // -- int constructor -- //

  @Test
  public void intConstructorSetsPower() {
    MachinesManager m = new MachinesManager(5);
    assertEquals(5, m.getPower());
  }

  @Test
  public void intConstructorClampsBelowZero() {
    MachinesManager m = new MachinesManager(-10);
    assertEquals(0, m.getPower());
  }

  @Test
  public void intConstructorClampsAboveMax() {
    MachinesManager m = new MachinesManager(100);
    assertEquals(14, m.getPower());
  }

  // -- setPower -- //

  @Test
  public void setPowerSetsValue() {
    manager.setPower(7);
    assertEquals(7, manager.getPower());
  }

  @Test
  public void setPowerClampsToZero() {
    manager.setPower(-5);
    assertEquals(0, manager.getPower());
  }

  @Test
  public void setPowerClampsToMax() {
    manager.setPower(100);
    assertEquals(14, manager.getPower());
  }

  // -- adjust -- //

  @Test
  public void adjustIncreasesPower() {
    manager.setPower(5);
    manager.adjust(3);
    assertEquals(8, manager.getPower());
  }

  @Test
  public void adjustDecreasesPower() {
    manager.setPower(10);
    manager.adjust(-3);
    assertEquals(7, manager.getPower());
  }

  @Test
  public void adjustDoesNotExceedMax() {
    manager.setPower(14);
    manager.adjust(10);
    assertEquals(14, manager.getPower());
  }

  @Test
  public void adjustDoesNotGoBelowZero() {
    manager.setPower(0);
    manager.adjust(-5);
    assertEquals(0, manager.getPower());
  }

  // -- hasRequiredPower -- //

  @Test
  public void hasRequiredPowerTrueWhenMet() {
    manager.setPower(5);
    assertTrue(manager.hasRequiredPower(5));
  }

  @Test
  public void hasRequiredPowerTrueWhenExceeded() {
    manager.setPower(10);
    assertTrue(manager.hasRequiredPower(3));
  }

  @Test
  public void hasRequiredPowerFalseWhenInsufficient() {
    manager.setPower(2);
    assertFalse(manager.hasRequiredPower(5));
  }

  // -- spawnSolarPanel -- //

  @Test
  public void spawnSolarPanelDeductsCost() {
    manager.setPower(14);
    manager.spawnSolarPanel(POSITION_A);
    assertEquals(14 - SolarPanel.COST, manager.getPower());
  }

  @Test
  public void spawnSolarPanelReturnsNonNullWhenAffordable() {
    manager.setPower(SolarPanel.COST);
    assertNotNull(manager.spawnSolarPanel(POSITION_A));
  }

  @Test
  public void spawnSolarPanelReturnsNullWhenInsufficient() {
    manager.setPower(SolarPanel.COST - 1);
    assertNull(manager.spawnSolarPanel(POSITION_A));
  }

  @Test
  public void spawnSolarPanelDoesNotDeductOnFailure() {
    manager.setPower(SolarPanel.COST - 1);
    int before = manager.getPower();
    manager.spawnSolarPanel(POSITION_A);
    assertEquals(before, manager.getPower());
  }

  // -- spawnLightningRod -- //

  @Test
  public void spawnLightningRodDeductsCost() {
    manager.setPower(14);
    manager.spawnLightningRod(POSITION_A);
    assertEquals(14 - LightningRod.COST, manager.getPower());
  }

  @Test
  public void spawnLightningRodReturnsNonNullWhenAffordable() {
    manager.setPower(LightningRod.COST);
    assertNotNull(manager.spawnLightningRod(POSITION_A));
  }

  @Test
  public void spawnLightningRodReturnsNullWhenInsufficient() {
    manager.setPower(0);
    assertNull(manager.spawnLightningRod(POSITION_A));
  }

  // -- spawnTeleporter -- //

  @Test
  public void spawnTeleporterDeductsCost() {
    manager.setPower(14);
    manager.spawnTeleporter(POSITION_A);
    assertEquals(14 - Teleporter.COST, manager.getPower());
  }

  @Test
  public void spawnTeleporterReturnsNonNullWhenAffordable() {
    manager.setPower(Teleporter.COST);
    assertNotNull(manager.spawnTeleporter(POSITION_A));
  }

  @Test
  public void spawnTeleporterReturnsNullWhenInsufficient() {
    manager.setPower(Teleporter.COST - 1);
    assertNull(manager.spawnTeleporter(POSITION_A));
  }

  // -- spawnPump -- //

  @Test
  public void spawnPumpDeductsCost() {
    manager.setPower(14);
    manager.spawnPump(POSITION_A, amount -> {});
    assertEquals(14 - Pump.COST, manager.getPower());
  }

  @Test
  public void spawnPumpReturnsNonNullWhenAffordable() {
    manager.setPower(Pump.COST);
    assertNotNull(manager.spawnPump(POSITION_A, amount -> {}));
  }

  @Test
  public void spawnPumpReturnsNullWhenInsufficient() {
    manager.setPower(Pump.COST - 1);
    assertNull(manager.spawnPump(POSITION_A, amount -> {}));
  }

  @Test
  public void spawnPumpDoesNotDeductOnFailure() {
    manager.setPower(Pump.COST - 1);
    int before = manager.getPower();
    manager.spawnPump(POSITION_A, amount -> {});
    assertEquals(before, manager.getPower());
  }

  // -- getNextTeleporterPosition -- //

  @Test
  public void singleTeleporterReturnsItsOwnPosition() {
    manager.setPower(14);
    manager.spawnTeleporter(POSITION_A);
    Positionable result = manager.getNextTeleporterPosition(POSITION_A);
    assertEquals(POSITION_A.getX(), result.getX());
    assertEquals(POSITION_A.getY(), result.getY());
  }

  @Test
  public void onCooldownReturnsExcludedPosition() {
    // Use a never-finishing timer so cooldown is always active
    TickTimer neverDone = new TickTimer() {
      @Override public void tick() {}
      @Override public boolean isFinished() { return false; }
    };
    MachinesManager m = new MachinesManager(neverDone);
    m.setPower(14);
    m.spawnTeleporter(POSITION_A);
    m.spawnTeleporter(POSITION_B);

    Positionable result = m.getNextTeleporterPosition(POSITION_A);
    assertEquals(POSITION_A.getX(), result.getX());
    assertEquals(POSITION_A.getY(), result.getY());
  }

  @Test
  public void offCooldownReturnsDifferentTeleporter() {
    // Use an always-finishing timer to bypass cooldown
    TickTimer alwaysDone = new TickTimer() {
      @Override public void tick() {}
      @Override public boolean isFinished() { return true; }
    };
    MachinesManager m = new MachinesManager(alwaysDone);
    m.setPower(14);
    m.spawnTeleporter(POSITION_A);
    m.spawnTeleporter(POSITION_B);

    Positionable result = m.getNextTeleporterPosition(POSITION_A);
    boolean isDifferent = result.getX() != POSITION_A.getX()
            || result.getY() != POSITION_A.getY();
    assertTrue("Should return a different teleporter when off cooldown", isDifferent);
  }
}