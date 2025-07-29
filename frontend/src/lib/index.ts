export type ISO8601 = string;
export type UUID = string;
export const enum EButtonType {
	button = "button",
	submit = "submit",
	reset = "reset",
	link = "link"
}
export type SpringBootErrorResponse = {
	error: string,
	message: string,
	path: string,
	status: number,
	timestamp: ISO8601,
}
export type APIResponse = {
	data: Record<string, any>,
	error: Record<string, any>,
	meta: Record<string, any> | null,
}
export type User = {
	uuid: UUID,
}
export type UserAPIResponse = APIResponse & {
	data: User
}
export type UserProfile = {
	userId: number,
	firstName: string,
	lastName: string,
	email: string,
	createdAt: ISO8601,
	updatedAt: ISO8601,
}
export type UserProfileAPIResponse = APIResponse & {
	data: UserProfile
}
export function getLocalDate(date: ISO8601): Date {
	const d = new Date(date);
	const offset = d.getTimezoneOffset();
	d.setMinutes(d.getMinutes() - offset);
	return d;
}
export function getLocalDateString(date: Date): string {
	const offset = date.getTimezoneOffset();
	date.setMinutes(date.getMinutes() + offset);
	return date.toISOString().split('T')[0];
}