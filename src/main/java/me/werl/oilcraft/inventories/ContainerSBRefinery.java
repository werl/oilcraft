package me.werl.oilcraft.inventories;

import me.werl.oilcraft.util.FuelUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ContainerSBRefinery extends Container {

    private final IInventory generator;
    private int burnTime;
    private int currentItemBurnTime;
    private int heat;
    private int maxHeat;


    public ContainerSBRefinery(InventoryPlayer playerInventory, IInventory generator) {
        this.generator = generator;

        this.addSlotToContainer(new SlotFurnaceFuel(generator, 0, 13, 39));
        this.addSlotToContainer(new Slot(generator, 1, 56, 19));
        this.addSlotToContainer(new Slot(generator, 2, 56, 51));
        this.addSlotToContainer(new Slot(generator, 3, 104, 19));
        this.addSlotToContainer(new Slot(generator, 4, 104, 51));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.generator);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for(int i = 0; i < this.listeners.size(); i++) {
            IContainerListener listener = this.listeners.get(i);

            if(this.burnTime != this.generator.getField(0)) {
                listener.sendProgressBarUpdate(this, 0, this.generator.getField(0));
            }
            if(this.currentItemBurnTime != this.generator.getField(1)) {
                listener.sendProgressBarUpdate(this, 1, this.generator.getField(1));
            }
            if(this.heat != this.generator.getField(2)) {
                listener.sendProgressBarUpdate(this, 2, this.generator.getField(2));
            }
            if(this.maxHeat != this.generator.getField(3)) {
                listener.sendProgressBarUpdate(this, 3, this.generator.getField(3));
            }
        }

        this.burnTime = this.generator.getField(0);
        this.currentItemBurnTime = this.generator.getField(1);
        this.heat = this.generator.getField(2);
        this.maxHeat = this.generator.getField(3);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        this.generator.setField(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return generator.isUseableByPlayer(playerIn);
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = null;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();

            if(index == 0) {
                if(!this.mergeItemStack(stackInSlot, 1, 37, true)) {
                    return null;
                }
            } else {
                if(FuelUtil.isBurnableInBoiler(stackInSlot)) {
                    if(!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                        return null;
                    }
                } else if(index >= 1 && index < 28) {
                    if(!this.mergeItemStack(stackInSlot, 28, 37, false)) {
                        return null;
                    }
                } else if(index >= 28 && index < 37 && !this.mergeItemStack(stackInSlot, 1, 28, false)) {
                    return null;
                } else if (this.mergeItemStack(stackInSlot, 1, 37, false)) {
                    return null;
                }
            }
            if (stackInSlot.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if(stackInSlot.stackSize == stack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(playerIn, stackInSlot);
        }

        return stack;
    }
}
