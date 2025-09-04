export const deleteShift = async (shiftId: string) => {

    const res = await fetch(`http://localhost:8080/shifts/${shiftId}`, {
        method: 'DELETE',
    });
    if (!res.ok) {
        throw new Error(`Failed to delete shift: ${shiftId}`);
    }

    return true;

};