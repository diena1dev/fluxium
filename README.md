# Fluxium
An ambitious mod that adds magicka-augmented items and machines.

**Supported Versions:**
```
- NeoForge 1.21.1
```

---
## Resources

### Fluxite

All Fluxium machines and items use Fluxite Shards, a crystal resource that rarely generates in natural in-world deposits. 

Fluxite Shards have varying size, purity, and charge.
- Size (tier, base quality) determines what machines it can be used in, provides a fixed boost to charge, and assigns a fixed quality (durability) to the gem.
- Purity (quality multiplier) determines how quickly quality degrades, and is influenced by charge.
- Charge (energy output) determines what sort of Magicka output the Shard emits, and remains constant until the Shard is depleted.

The size of Shards is randomized in natural generation. Large Shards are exceedingly rare, while small to medium Shards are abundant.
Because a handful of Fluxium machines require large Shards (like the Fluxite Synthesizer),
smaller Fluxite Shards can be condensed together into a larger Shard, at the cost of purity and charge.

As briefly mentioned, Fluxite Shards' purity functions as a durability multiplier.
Essentially, a Shard of high purity with high charge will degrade slower than a Shard of low purity the same charge.

As mentioned, Fluxite Shards emit a fixed amount of energy until their quality is fully degraded, at which point they stop emitting Magicka.
The Magicka output of a Shard determines what it can be used in, and can be augmented by certain machines.

Medium and large Fluxite Shards *cannot* be picked up as items, they only have a physical state in the world!
To move bigger Shards, use the Fluxite Manipulator, which can change their position and insert them into Fluxite Interfaces

### Magicka

Magicka (star-essence) is the source of energy that Fluxium machines and items are powered off of.
It originates from stars, and exists everywhere. Fluxite Shards accumulate that energy into a tangible, harnessable form.

---

## Machines

### Production/Resource Generation
    Fluxite Accumulator
    - Multiblock machine, consists of a base platform and accumulator pylons
    - When fed a *steady* stream of FE and gems (diamonds, emeralds, etc.), it grows a Fluxite deposit between the pylons.
    - It requires large Fluxite Shards to function.

### Offense

### Defense
    Fluxite Shield | TODO: New Name.
    - Configurable area shield that stops projectiles or explosions from entering. (Projectiles and explosions on the inside of the shield still function, along with being able to shoot out from behind the shield.)
    - It's powered off of Fluxite Shards. The larger the protected area, the more Magicka is required to upkep the shield. Because of this, a smaller shield only requires a handful of small Fluxite Shards.

### Transport
    Antiphysic Dimensional Rift Gate
    - Multiblock machine, consists of a Shard interface, Rift Gate, and control panel.
    - Requires **seven** of the largest Fluxite Shards the Accumulator can produce to open a rift.
    - Once a dimensional rift is open, it requires Magicka to remain stable. If the rift loses sufficient charge to remain stable, it will destabilize and implode, bringing most of the neighboring reality with it.
    - Can open a two-way rift to any location in any dimension. If the destination is missing Rift Stabilizers, the nearby environment will begin to be converted into Fluxite deposits and nearby mobs sucked through.

### Misc/Not-Machines
    Fluxite Interface
    - Accepts all sizes of Fluxite Shards, can be deposited into using a Fluxite Manipulator.
    - Port for some Multiblocks that require Fluxite Shards.

---

## Items

### Tools
    Fluxite Manipulator
    - A claw-like tool powered by small Fluxite Shards.
    - Used to carry around and insert Fluxite Crystals larger than medium size in the world and into machines.