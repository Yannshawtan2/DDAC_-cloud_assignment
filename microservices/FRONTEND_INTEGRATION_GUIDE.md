# Frontend Integration Guide

## Overview
Both the **Maintenance Notification Service** and **Template Service** now use standard REST APIs. Your frontend can call these endpoints directly without any wrapper clients.

## Base URLs
```
Maintenance Notifications: https://your-api-gateway-url/maintenance-notifications
Template Service: https://your-api-gateway-url/entities
```

## Example Frontend Integration

### JavaScript/TypeScript Examples

#### 1. Get All Maintenance Notifications
```javascript
// Get all notifications
const response = await fetch('/maintenance-notifications');
const notifications = await response.json();

// Get only active notifications
const activeResponse = await fetch('/maintenance-notifications?active=true');
const activeNotifications = await activeResponse.json();
```

#### 2. Create New Notification
```javascript
const newNotification = {
    title: "System Maintenance",
    message: "The system will be down for 2 hours",
    priority: "HIGH"
};

const response = await fetch('/maintenance-notifications', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify(newNotification)
});

if (response.ok) {
    const createdNotification = await response.json();
    console.log('Created:', createdNotification);
} else {
    const error = await response.json();
    console.error('Error:', error.error);
}
```

#### 3. Update Notification
```javascript
const updateData = {
    title: "Updated Maintenance Notice",
    priority: "CRITICAL"
};

const response = await fetch(`/maintenance-notifications/${id}`, {
    method: 'PUT',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify(updateData)
});
```

#### 4. Activate/Deactivate Notification
```javascript
// Activate notification (only one can be active)
const activateResponse = await fetch(`/maintenance-notifications/${id}/activate`, {
    method: 'POST'
});

// Deactivate notification
const deactivateResponse = await fetch(`/maintenance-notifications/${id}/deactivate`, {
    method: 'POST'
});
```

## React Hook Example

```typescript
import { useState, useEffect } from 'react';

interface MaintenanceNotification {
    id: number;
    title: string;
    message: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
    createdAt: string;
    isActive: boolean;
}

export const useMaintenanceNotifications = () => {
    const [notifications, setNotifications] = useState<MaintenanceNotification[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchNotifications = async (activeOnly = false) => {
        setLoading(true);
        try {
            const url = activeOnly 
                ? '/maintenance-notifications?active=true'
                : '/maintenance-notifications';
            const response = await fetch(url);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            setNotifications(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
        } finally {
            setLoading(false);
        }
    };

    const createNotification = async (notification: Omit<MaintenanceNotification, 'id' | 'createdAt' | 'isActive'>) => {
        try {
            const response = await fetch('/maintenance-notifications', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(notification)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || 'Failed to create notification');
            }
            
            const created = await response.json();
            setNotifications(prev => [...prev, created]);
            return created;
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
            throw err;
        }
    };

    const activateNotification = async (id: number) => {
        try {
            const response = await fetch(`/maintenance-notifications/${id}/activate`, {
                method: 'POST'
            });
            
            if (!response.ok) {
                throw new Error('Failed to activate notification');
            }
            
            // Refresh notifications to update state
            fetchNotifications();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
            throw err;
        }
    };

    useEffect(() => {
        fetchNotifications();
    }, []);

    return {
        notifications,
        loading,
        error,
        fetchNotifications,
        createNotification,
        activateNotification
    };
};
```

## Error Handling

All endpoints return consistent error responses:

```json
{
    "error": "Error message description"
}
```

HTTP Status Codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation errors)
- `404` - Not Found
- `500` - Internal Server Error

## Priority Values
```typescript
type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
```

## Complete API Reference

### Maintenance Notifications
```
GET    /maintenance-notifications              # List all
GET    /maintenance-notifications?active=true # Active only
GET    /maintenance-notifications/{id}        # Get by ID
POST   /maintenance-notifications             # Create
PUT    /maintenance-notifications/{id}        # Update
DELETE /maintenance-notifications/{id}        # Delete
POST   /maintenance-notifications/{id}/activate   # Activate
POST   /maintenance-notifications/{id}/deactivate # Deactivate
GET    /maintenance-notifications/priority/{priority} # By priority
```

### Template Service
```
GET    /entities                  # List all
GET    /entities?name={name}      # Search by name
GET    /entities/{id}             # Get by ID
POST   /entities                  # Create
PUT    /entities/{id}             # Update
DELETE /entities/{id}             # Delete
GET    /entities/search?name={name} # Search
GET    /entities/exists/{id}      # Check existence
```

No additional client libraries or wrappers are needed - just standard HTTP calls!
