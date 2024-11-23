interface StatusBarProps {
    lastRetrievedTime: Date | null;
}

function StatusBar({ lastRetrievedTime }: StatusBarProps) {
    // Formatting the time
    const formattedTime = (date: Date) => {
        return new Intl.DateTimeFormat("en-US", {
            hour: "numeric",
            minute: "numeric",
            second: "numeric",
            hour12: true,
        }).format(date);
    };

    return (
        <div className="bg-white shadow rounded-lg p-4 mt-4">
            <div className="flex justify-between items-center">
                <div className="text-sm text-gray-600">
                    {lastRetrievedTime ? (
                        <span>
                            Last retrieved: {formattedTime(lastRetrievedTime)}
                        </span>
                    ) : (
                        <span>Data not retrieved yet</span>
                    )}
                </div>
            </div>
        </div>
    );
}

export default StatusBar;
