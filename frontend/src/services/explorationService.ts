import { Coordinates, ExplorationSession } from '../types';

/**
 * Calculates the great circle distance between two points in kilometers using Haversine formula
 */
export function calculateHaversineDistanceKm(coord1: Coordinates, coord2: Coordinates): number {
  const R = 6371; // Earth's radius in kilometers
  const dLat = ((coord2.latitude - coord1.latitude) * Math.PI) / 180;
  const dLon = ((coord2.longitude - coord1.longitude) * Math.PI) / 180;

  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos((coord1.latitude * Math.PI) / 180) *
      Math.cos((coord2.latitude * Math.PI) / 180) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

export function formatDistance(km: number): string {
  if (km < 1) {
    return `${Math.round(km * 1000)} m`;
  }
  return `${km.toFixed(2)} km`;
}

export function formatDuration(seconds: number): string {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  if (mins >= 60) {
    const hours = Math.floor(mins / 60);
    const remainingMins = mins % 60;
    return `${hours}h ${remainingMins}m`;
  }
  return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
}

export function createInitialSession(): ExplorationSession {
  return {
    id: `session_${Date.now()}`,
    startTime: new Date().toISOString(),
    distanceKm: 0,
    durationSeconds: 0,
    questsCompletedCount: 0,
    discoveriesCount: 0,
    xpEarned: 0,
    coinsEarned: 0,
    status: 'active',
  };
}
