import Foundation
import os

struct DeviceStats {
    let ramTotalBytes: UInt64
    let ramUsedBytes: UInt64
    let ramLoadPercent: Int
    let storageTotalBytes: Int64
    let storageFreeBytes: Int64
    let storageUsedPercent: Int

    static func snapshot() -> DeviceStats {
        let ramTotal = ProcessInfo.processInfo.physicalMemory
        let available = max(0, os_proc_available_memory())
        let ramAvailable = UInt64(available)
        let ramUsed = ramTotal > ramAvailable ? ramTotal - ramAvailable : 0
        let ramPercent = ramTotal > 0 ? Int((ramUsed * 100) / ramTotal) : 0

        let (diskTotal, diskFree) = diskStats()
        let diskUsed = diskTotal > diskFree ? diskTotal - diskFree : 0
        let diskPercent = diskTotal > 0 ? Int((diskUsed * 100) / diskTotal) : 0

        return DeviceStats(
            ramTotalBytes: ramTotal,
            ramUsedBytes: ramUsed,
            ramLoadPercent: min(max(ramPercent, 0), 100),
            storageTotalBytes: diskTotal,
            storageFreeBytes: diskFree,
            storageUsedPercent: min(max(diskPercent, 0), 100)
        )
    }

    private static func diskStats() -> (Int64, Int64) {
        let url = URL(fileURLWithPath: NSHomeDirectory())
        let keys: [URLResourceKey] = [
            .volumeTotalCapacityKey,
            .volumeAvailableCapacityForImportantUsageKey
        ]
        guard let values = try? url.resourceValues(forKeys: Set(keys)),
              let total = values.volumeTotalCapacity,
              let free = values.volumeAvailableCapacityForImportantUsage else {
            return (0, 0)
        }
        return (Int64(total), Int64(free))
    }
}

func formatBytes(_ bytes: Int64) -> String {
    guard bytes > 0 else { return "0 B" }
    let units = ["B", "KB", "MB", "GB", "TB"]
    var value = Double(bytes)
    var i = 0
    while value >= 1024 && i < units.count - 1 {
        value /= 1024
        i += 1
    }
    return i == 0
        ? String(format: "%.0f %@", value, units[i])
        : String(format: "%.1f %@", value, units[i])
}
